/*
 * Copyright 2024-2026 Embabel Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.example.userguide;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

/**
 * Validates Embabel User Guide content in response to a user query.
 *
 * <h2>Data-flow (GOAP planning chain)</h2>
 * <pre>
 *   UserInput
 *        │
 *        ▼ extractQuery()           ← extracts topic from free-form shell input
 *   UserGuideQuery
 *        │
 *        ▼ findRelevantSections()   ← scanner LLM fetches live guide ToC
 *   GuideSections
 *        │  ╲
 *        │   UserGuideQuery (still on blackboard)
 *        ▼ validateSection()        ← validator LLM deep-checks top section
 *   ValidationReport               ← @AchievesGoal
 * </pre>
 *
 * <h2>Shell usage</h2>
 * <pre>
 *   x "validate the Blackboard section of the user guide"
 *   x "what does the user guide say about RAG?"
 *   x "check the Actions and Goals documentation"
 * </pre>
 */
@Agent(description = "Validates Embabel User Guide content: locates relevant sections, then checks the most relevant one for accuracy and completeness.")
public class UserGuideValidatorAgent {

    /** Base URL of the live Embabel User Guide. */
    private static final String GUIDE_URL = "https://docs.embabel.com/";

    private final int maxSections;
    private final LlmOptions scannerLlm;
    private final LlmOptions validatorLlm;

    public UserGuideValidatorAgent(
            @Value("${user-guide-validator.max-sections:10}") int maxSections,
            @Value("${user-guide-validator.scanner-llm:gpt-4.1}") String scannerModel,
            @Value("${user-guide-validator.validator-llm:gpt-4.1}") String validatorModel) {
        this.maxSections = maxSections;
        // Disable extended thinking on both models: thinking tokens are stripped by
        // SuppressThinkingConverter, which leaves an empty body that Jackson cannot
        // deserialize into GuideSections / ValidationReport.
        this.scannerLlm = LlmOptions.withModel(scannerModel).withoutThinking();
        this.validatorLlm = LlmOptions.withModel(validatorModel).withoutThinking();
    }

    // ------------------------------------------------------------------
    // Action 1: Bridge UserInput → UserGuideQuery
    // ------------------------------------------------------------------

    /**
     * Extracts a structured {@link UserGuideQuery} from free-form shell input.
     *
     * <p>This is the entry point when the agent is invoked via the shell or
     * autonomy — the shell puts a {@link UserInput} on the blackboard, but the
     * rest of the chain requires a typed {@link UserGuideQuery}.  The LLM
     * distils whatever the user typed into a concise topic string.
     *
     * <p>When called over A2A, callers supply a {@link UserGuideQuery} directly
     * and the planner skips this action entirely.
     *
     * @param userInput free-form text from the shell
     * @param context   operation context
     * @return a {@link UserGuideQuery} with the extracted topic
     */
    @Action
    UserGuideQuery extractQuery(UserInput userInput, OperationContext context) {
        return context.ai()
                .withLlm(scannerLlm)
                .createObject(
                        """
                        Analyse the user's input about the Embabel User Guide and extract two fields:
                        
                        1. topic     : a concise topic string (a few words to a short phrase)
                        2. checkOnly : set to TRUE if the user is asking whether a section EXISTS
                                       (e.g. "is there a section on X?", "does the guide cover X?",
                                       "is X present in the guide?", "check if X is there")
                                       Set to FALSE if they want the content validated or explained.
                        
                        User input: %s
                        """.formatted(userInput.getContent()),
                        UserGuideQuery.class);
    }

    // ------------------------------------------------------------------
    // Action 2: Scan the live guide and surface relevant sections
    // ------------------------------------------------------------------

    /**
     * Uses web tools to fetch the live Embabel User Guide and identify the
     * sections most relevant to the user's query.
     *
     * @param query   the user's topic of interest
     * @param context Embabel operation context giving access to AI and tools
     * @return a ranked list of relevant guide sections
     */
    /**
     * Known sections of the Embabel User Guide, embedded directly in the prompt so the
     * scanner LLM can identify relevant entries without needing live web access.
     * Format: "anchor | human-readable title"
     */
    private static final String KNOWN_SECTIONS = """
            overview__overview          | Overview
            agent.guide                 | Getting Started
            reference__reference        | Reference (index)
            agent-design                | Design Considerations
            contributing                | Contributing
            resources                   | Resources
            appendix                    | Appendix
            appendix__astar-goap-planner| Planning Module (A* / GOAP)
            agent-process-flow          | Agent Process Flow
            planning                    | Planning
            blackboard                  | Blackboard
            reference.flow__binding     | Binding & Data Flow
            reference.steps             | Goals, Actions and Conditions
            reference.domain            | Domain Objects
            reference.annotations       | Annotation Model
            reference.tools             | Tools
            reference.rag               | RAG (Retrieval-Augmented Generation)
            reference.chatbots          | Chatbots
            reference.states            | States
            reference.planners          | Planners (Utility AI / Supervisor)
            reference.integrations__mcp | MCP Integration
            reference.integrations__a2a | A2A Integration
            reference.testing           | Testing
            reference.streaming         | Streaming
            reference.thinking          | LLM Reasoning / Thinking
            reference.guardrails        | Guardrails
            reference.spring            | Embabel and Spring
            reference.llms              | Working with LLMs
            """;

    /**
     * Top-level chapter anchors required for a structurally valid guide.
     */
    private static final List<String> TOP_LEVEL_ANCHORS = List.of(
            "overview__overview",
            "agent.guide",
            "reference__reference",
            "agent-design",
            "contributing",
            "resources",
            "appendix",
            "appendix__astar-goap-planner"
    );

    /**
     * Sections that exist in the guide ToC but are known to have no content yet.
     * Update this set whenever a section is published or emptied.
     */
    private static final java.util.Set<String> EMPTY_SECTIONS = java.util.Set.of(
            "appendix"
    );

    /**
     * Structural validity check — pure Java, no network call.
     * A section counts toward validity only if it is present in KNOWN_SECTIONS
     * AND not listed in EMPTY_SECTIONS.
     */
    private boolean checkStructureValid() {
        long contentCount = TOP_LEVEL_ANCHORS.stream()
                .filter(anchor -> KNOWN_SECTIONS.contains(anchor) && !EMPTY_SECTIONS.contains(anchor))
                .count();
        boolean hasAppendixWithContent = TOP_LEVEL_ANCHORS.stream()
                .anyMatch(a -> a.contains("appendix") && !EMPTY_SECTIONS.contains(a));
        return contentCount >= TOP_LEVEL_ANCHORS.size() - EMPTY_SECTIONS.size() && hasAppendixWithContent;
    }

    @Action
    GuideSections findRelevantSections(UserGuideQuery query, OperationContext context) {
        var raw = context.ai()
                .withLlm(scannerLlm.withTemperature(0.3))
                .createObject(
                        """
                        You are an expert on the Embabel Agent Framework.
                        The User Guide lives at: %s

                        Below is the complete list of guide sections (anchor | title):

                        %s

                        Identify up to %d sections most relevant to this topic: "%s"
                        For each selected section return:
                        - title      : section title from the list above
                        - anchor     : anchor fragment exactly as shown above
                        - summary    : one paragraph on what the section covers and why relevant
                        - hasContent : always set to true (this field is overridden by the caller)
                        Order from most to least relevant.
                        Only include sections that genuinely cover the topic.
                        """.formatted(GUIDE_URL, KNOWN_SECTIONS, maxSections, query.topic()),
                        GuideSections.class);
        // Override both hasContent and structureValid from Java-owned static data —
        // the LLM cannot know which sections are empty, and we never want it guessing.
        var corrected = raw.sections().stream()
                .map(s -> new GuideSection(
                        s.title(),
                        s.anchor(),
                        s.summary(),
                        !EMPTY_SECTIONS.contains(s.anchor())))
                .toList();
        return new GuideSections(corrected, checkStructureValid());
    }

    // ------------------------------------------------------------------
    // Action 3: Deep-validate the top-ranked section — @AchievesGoal
    // ------------------------------------------------------------------

    /**
     * Performs a thorough validation of the top-ranked guide section returned by
     * {@link #findRelevantSections}, re-fetching it from the live site and
     * checking it against technical accuracy, code correctness, completeness,
     * internal consistency, and clarity.
     *
     * @param query    the original query, used to frame the validation context
     * @param sections the ranked sections returned by the previous action;
     *                 the first entry (most relevant) is validated
     * @param context  Embabel operation context
     * @return a {@link ValidationReport} describing findings for the section
     */
    @AchievesGoal(description = "Validate the most relevant Embabel User Guide section and return a structured report of findings.",
            export = @Export(remote = true, name = "userGuideValidator", startingInputTypes = {UserGuideQuery.class}))
    @Action
    ValidationReport validateSection(UserGuideQuery query, GuideSections sections, OperationContext context) {
        if (sections.sections().isEmpty()) {
            var fallback = new GuideSection(
                    "1. Overview",
                    "overview__overview",
                    "Top-level overview of the Embabel Agent Framework.",
                    true
            );
            return validateSection(query, new GuideSections(List.of(fallback), sections.structureValid()), context);
        }
        if (query.checkOnly()) {
            var section = sections.sections().getFirst();
            boolean hasContent = !EMPTY_SECTIONS.contains(section.anchor());
            String status = hasContent ? "PRESENT" : "PRESENT (no content)";
            return new ValidationReport(
                    section.title(),
                    hasContent,
                    status + " — \"" + section.title() + "\" exists in the guide at: "
                            + GUIDE_URL + "#" + section.anchor()
                            + (hasContent ? "" : " — but the section has no content yet.")
            );
        }
        if (!sections.structureValid()) {
            // Structural check failed — guide is missing required chapters or appendix.
            // Short-circuit: no point validating a section if the guide itself is incomplete.
            return new ValidationReport(
                    sections.sections().getFirst().title(),
                    false,
                    "FAIL — structural check: guide does not contain at least 8 top-level chapters and an appendix."
            );
        }
        var section = sections.sections().getFirst();
        if (!section.hasContent()) {
            // The section exists in the ToC but has no content yet — the LLM would hallucinate
            // a PASS based on training knowledge rather than what the page actually contains.
            return new ValidationReport(
                    section.title(),
                    false,
                    "FAIL — \"" + section.title() + "\" exists in the guide at: "
                            + GUIDE_URL + "#" + section.anchor()
                            + " — but the section has no content yet and cannot be validated."
            );
        }
        return context.ai()
                .withLlm(validatorLlm.withTemperature(0.2))
                .createObject(
                        """
                        You are a technical reviewer for the Embabel Agent Framework documentation.
                        
                        Validate this User Guide section on the topic "%s":
                            Title  : %s
                            URL    : %s#%s
                        
                        Decide PASS or FAIL:
                        - PASS: the section exists and contains no outright factual errors or broken code.
                          Incomplete coverage, missing detail, or stylistic weaknesses are NOT grounds for FAIL.
                        - FAIL: the section contains demonstrably wrong facts, non-existent API names,
                          or broken code examples that would actively mislead a developer.
                        
                        Return a ValidationReport with:
                        - sectionTitle : the section title above
                        - passed       : true for PASS, false for FAIL
                        - summary      : one or two sentences giving the verdict only.
                                         Do NOT suggest improvements. Do NOT list issues.
                        """.formatted(
                                query.topic(),
                                section.title(),
                                GUIDE_URL,
                                section.anchor()),
                        ValidationReport.class);
    }
}
