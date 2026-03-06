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

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.test.unit.FakeOperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.embabel.agent.core.ToolGroupRequirement;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link UserGuideValidatorAgent}.
 *
 * <h2>Testing philosophy</h2>
 * <p>Following the pattern established by {@code StarNewsFinderTest}, each test
 * verifies the <em>shape of LLM interactions</em> rather than their outputs:
 * <ul>
 *   <li>Prompts contain the data they are supposed to embed.</li>
 *   <li>The correct tool groups are (or are not) attached.</li>
 *   <li>Known User Guide chapters are represented in the scanner prompt.</li>
 * </ul>
 *
 * <p>Think of this like checking a chef's mise en place before service:
 * we are not tasting the finished dish, we are confirming every ingredient
 * is on the board before the cooking starts.
 */
class UserGuideValidatorAgentTest {

    // -----------------------------------------------------------------------
    // Canonical list of major User Guide chapters / anchors.
    // These are derived from the live Table of Contents at docs.embabel.com
    // and act as the "known chapters" the tests validate against.
    // -----------------------------------------------------------------------

    /**
     * Top-level chapter anchors as they appear in the live User Guide ToC.
     * Derived from the 8 top-level bullet entries in the HTML Table of Contents.
     * Add new entries here whenever a chapter is added to the guide.
     */
    static final List<String> KNOWN_TOP_LEVEL_CHAPTERS = List.of(
            "overview__overview",          // 1. Overview
            "agent.guide",                 // 2. Getting Started
            "reference__reference",        // 3. Reference
            "agent-design",                // 4. Design Considerations
            "contributing",                // 5. Contributing
            "resources",                   // 6. Resources
            "appendix",                    // 7. Appendix
            "appendix__astar-goap-planner" // 8. Planning Module
    );

    /**
     * Key reference sub-section anchors the agent must be able to find.
     * This list mirrors the most important sections from section 3 of the guide.
     */
    static final List<String> KNOWN_REFERENCE_SECTIONS = List.of(
            "agent-process-flow",
            "planning",
            "blackboard",
            "reference.flow__binding",
            "reference.steps",            // Goals, Actions and Conditions
            "reference.domain",           // Domain Objects
            "reference.annotations",      // Annotation model
            "reference.tools",            // Tools
            "reference.rag",              // RAG
            "reference.chatbots",         // Chatbots
            "reference.states",           // States
            "reference.planners",         // Planners (Utility AI / Supervisor)
            "reference.integrations__mcp",// MCP
            "reference.integrations__a2a",// A2A
            "reference.testing",          // Testing
            "reference.streaming",        // Streaming
            "reference.thinking",         // LLM Reasoning / Thinking
            "reference.guardrails",       // Guardrails
            "reference.spring",           // Embabel and Spring
            "reference.llms"              // Working with LLMs
    );

    /** Convenience stream of all known anchors, used by parameterised tests. */
    static Stream<String> allKnownAnchors() {
        return Stream.concat(
                KNOWN_TOP_LEVEL_CHAPTERS.stream(),
                KNOWN_REFERENCE_SECTIONS.stream()
        );
    }

    // -----------------------------------------------------------------------
    // Shared fixtures
    // -----------------------------------------------------------------------

    private static final String GUIDE_URL = "https://docs.embabel.com/";

    private UserGuideValidatorAgent agent;
    private FakeOperationContext fakeContext;

    @BeforeEach
    void setUp() {
        agent = new UserGuideValidatorAgent(10, "gpt-4o-mini", "gpt-4o-mini");
        fakeContext = new FakeOperationContext();
    }

    // -----------------------------------------------------------------------
    // Action 1 – extractQuery (UserInput → UserGuideQuery)
    // -----------------------------------------------------------------------

    @Nested
    class ExtractQuery {

        @Test
        void promptMustContainUserInputContent() {
            fakeContext.expectResponse(new UserGuideQuery("blackboard"));

            agent.extractQuery(new UserInput("tell me about the blackboard"), fakeContext);

            assertTrue(firstPrompt().contains("blackboard"),
                    "extractQuery prompt must embed the raw user input content");
        }

        @Test
        void mustNotUseWebToolGroup() {
            fakeContext.expectResponse(new UserGuideQuery("tools"));

            agent.extractQuery(new UserInput("validate the tools section"), fakeContext);

            assertTrue(firstToolGroups().isEmpty(),
                    "extractQuery must not attach web tools — it is a pure extraction step");
        }
    }

    // -----------------------------------------------------------------------
    // Action 2 – findRelevantSections
    // -----------------------------------------------------------------------

    @Nested
    class FindRelevantSections {

        @Test
        void promptMustContainGuideUrl() {
            fakeContext.expectResponse(new GuideSections(List.of(), true));

            agent.findRelevantSections(new UserGuideQuery("blackboard"), fakeContext);

            var prompt = firstPrompt();
            assertTrue(prompt.contains(GUIDE_URL),
                    "Scanner prompt must contain the guide URL so the LLM knows where to fetch");
        }

        @Test
        void promptMustContainQueryTopic() {
            fakeContext.expectResponse(new GuideSections(List.of(), true));
            var topic = "Goal-Oriented Action Planning";

            agent.findRelevantSections(new UserGuideQuery(topic), fakeContext);

            var prompt = firstPrompt();
            assertTrue(prompt.contains(topic),
                    "Scanner prompt must embed the user's topic verbatim");
        }

        @Test
        void promptMustContainMaxSectionsConstant() {
            fakeContext.expectResponse(new GuideSections(List.of(), true));

            agent.findRelevantSections(new UserGuideQuery("tools"), fakeContext);

            var prompt = firstPrompt();
            assertTrue(prompt.contains("10"),
                    "Scanner prompt must embed the hardcoded MAX_SECTIONS value (10)");
        }

        @Test
        void promptMustNotAskLlmToCheckStructure() {
            // Structural validity is now computed in Java from TOP_LEVEL_ANCHORS —
            // the LLM prompt must NOT contain fetch instructions or structural directives.
            fakeContext.expectResponse(new GuideSections(List.of(), true));

            agent.findRelevantSections(new UserGuideQuery("blackboard"), fakeContext);

            var prompt = firstPrompt();
            assertAll("Scanner prompt must delegate structural check to Java, not LLM",
                    () -> assertTrue(prompt.contains("hasContent"),
                            "must still reference hasContent field for section results"),
                    () -> assertFalse(prompt.contains("structureValid"),
                            "structureValid is set by Java — must not appear in LLM prompt"),
                    () -> assertFalse(prompt.contains("fetch"),
                            "prompt must not instruct the LLM to fetch anything")
            );
        }

        @Test
        void checkStructureValidReturnsTrueForCompleteList() {
            // checkStructureValid() is package-private via the agent instance;
            // we verify it by observing the structureValid flag on the returned GuideSections.
            fakeContext.expectResponse(new GuideSections(List.of(), false));

            var result = agent.findRelevantSections(new UserGuideQuery("overview"), fakeContext);

            assertTrue(result.structureValid(),
                    "structureValid must be true when KNOWN_SECTIONS contains all TOP_LEVEL_ANCHORS");
        }

        /**
         * Verifies that each known chapter anchor is a plausible search term that
         * would be returned by a correctly-prompted LLM.  Concretely, we assert that
         * when the topic IS that anchor string, the prompt embeds it — confirming the
         * agent will not silently drop known chapter names.
         */
        @ParameterizedTest(name = "topic \"{0}\" appears in scanner prompt")
        @MethodSource("com.embabel.example.userguide.UserGuideValidatorAgentTest#allKnownAnchors")
        void knownChapterTopicIsEmbeddedInPrompt(String anchor) {
            fakeContext.expectResponse(new GuideSections(List.of(), true));

            agent.findRelevantSections(new UserGuideQuery(anchor), fakeContext);

            var prompt = firstPrompt();
            assertTrue(prompt.contains(anchor),
                    "Known chapter anchor '%s' must appear in the scanner prompt".formatted(anchor));
        }
    }

    // -----------------------------------------------------------------------
    // Action 2 – validateSection
    // -----------------------------------------------------------------------

    @Nested
    class ValidateSection {

        @Test
        void checkOnlyReturnsPresentWithoutLlmCall() {
            var sections = sampleSections(); // structureValid=true, blackboard has content

            var result = agent.validateSection(
                    new UserGuideQuery("blackboard", true),
                    sections,
                    fakeContext);

            assertTrue(result.passed(),   "Presence check of a section with content must pass");
            assertTrue(result.summary().contains("PRESENT"),
                    "Summary must say PRESENT for a checkOnly query");
            assertTrue(result.summary().contains("blackboard"),
                    "Summary must include the section anchor/URL");
            assertTrue(fakeContext.getLlmInvocations().isEmpty(),
                    "checkOnly must not make any LLM call");
        }

        @Test
        void checkOnlyFailsForEmptySection() {
            var emptySections = new GuideSections(List.of(
                    new GuideSection("Appendix", "appendix", "Appendix placeholder.", false)
            ), true);

            var result = agent.validateSection(
                    new UserGuideQuery("appendix", true),
                    emptySections,
                    fakeContext);

            assertFalse(result.passed(),
                    "Presence check of a known-empty section must fail");
            assertTrue(result.summary().contains("no content"),
                    "Summary must mention that the section has no content");
            assertTrue(fakeContext.getLlmInvocations().isEmpty(),
                    "checkOnly must not make any LLM call even for empty sections");
        }

        @Test
        void structureInvalidCausesImmediateFail() {
            // structureValid=false → should short-circuit without any LLM call
            var sections = new GuideSections(List.of(
                    new GuideSection("3.2.3. Blackboard", "blackboard", "", true)
            ), false);

            var result = agent.validateSection(new UserGuideQuery("blackboard"), sections, fakeContext);

            assertFalse(result.passed(), "Must fail when structureValid is false");
            assertTrue(result.summary().contains("structural check"),
                    "Summary must mention the structural check failure");
            assertTrue(fakeContext.getLlmInvocations().isEmpty(),
                    "No LLM call should be made when structural check fails");
        }

        @Test
        void emptySectonsFallsBackToOverview() {
            fakeContext.expectResponse(sampleReport(new GuideSection("1. Overview", "overview__overview", "", true)));

            var result = agent.validateSection(
                    new UserGuideQuery("something obscure"),
                    new GuideSections(List.of(), true),
                    fakeContext);

            assertNotNull(result, "Must return a report even when sections list is empty");
            assertTrue(firstPrompt().contains("overview__overview"),
                    "Fallback must validate the Overview section");
        }

        @Test
        void promptMustContainSectionTitle() {
            var sections = sampleSections();
            fakeContext.expectResponse(sampleReport(sections.sections().getFirst()));

            agent.validateSection(new UserGuideQuery("blackboard"), sections, fakeContext);

            assertTrue(firstPrompt().contains(sections.sections().getFirst().title()),
                    "Validator prompt must embed the top-ranked section title");
        }

        @Test
        void promptMustContainSectionAnchor() {
            var sections = sampleSections();
            fakeContext.expectResponse(sampleReport(sections.sections().getFirst()));

            agent.validateSection(new UserGuideQuery("blackboard"), sections, fakeContext);

            assertTrue(firstPrompt().contains(sections.sections().getFirst().anchor()),
                    "Validator prompt must embed the top-ranked section anchor");
        }

        @Test
        void promptMustContainGuideUrlWithAnchorFragment() {
            var sections = sampleSections();
            var topAnchor = sections.sections().getFirst().anchor();
            fakeContext.expectResponse(sampleReport(sections.sections().getFirst()));

            agent.validateSection(new UserGuideQuery("blackboard"), sections, fakeContext);

            var expectedUrl = GUIDE_URL + "#" + topAnchor;
            assertTrue(firstPrompt().contains(expectedUrl),
                    "Validator prompt must contain the full URL with anchor fragment");
        }

        @Test
        void promptMustContainOriginalQueryTopic() {
            var topic = "blackboard internals";
            var sections = sampleSections();
            fakeContext.expectResponse(sampleReport(sections.sections().getFirst()));

            agent.validateSection(new UserGuideQuery(topic), sections, fakeContext);

            assertTrue(firstPrompt().contains(topic),
                    "Validator prompt must keep the original query topic for framing context");
        }

        @Test
        void promptMustMentionPassFailCriteria() {
            var sections = sampleSections();
            fakeContext.expectResponse(sampleReport(sections.sections().getFirst()));

            agent.validateSection(new UserGuideQuery("blackboard"), sections, fakeContext);

            var prompt = firstPrompt();
            assertAll("PASS/FAIL criteria must appear in the validator prompt",
                    () -> assertTrue(prompt.contains("PASS"), "missing PASS"),
                    () -> assertTrue(prompt.contains("FAIL"), "missing FAIL")
            );
        }

        /**
         * For every known reference section, confirm the agent can construct a
         * valid validation prompt when that section is ranked first.
         */
        @ParameterizedTest(name = "validator prompt is well-formed for anchor \"{0}\"")
        @MethodSource("com.embabel.example.userguide.UserGuideValidatorAgentTest#allKnownAnchors")
        void validatorPromptIsWellFormedForKnownChapter(String anchor) {
            var section = new GuideSection(
                    "Section: " + anchor,
                    anchor,
                    "Summary for " + anchor,
                    true
            );
            var sections = new GuideSections(List.of(section), true);
            fakeContext.expectResponse(sampleReport(section));

            agent.validateSection(new UserGuideQuery(anchor), sections, fakeContext);

            var prompt = firstPrompt();
            assertAll("Prompt must be well-formed for anchor '%s'".formatted(anchor),
                    () -> assertTrue(prompt.contains(anchor),    "anchor must appear in prompt"),
                    () -> assertTrue(prompt.contains(GUIDE_URL), "guide URL must appear in prompt")
            );
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Returns the text of the first (and usually only) LLM invocation's prompt. */
    private String firstPrompt() {
        var invocations = fakeContext.getLlmInvocations();
        assertFalse(invocations.isEmpty(), "Expected at least one LLM invocation");
        return invocations.getFirst().getMessages().getFirst().getContent();
    }

    /** Returns the tool group requirements attached to the first LLM invocation. */
    private Set<ToolGroupRequirement> firstToolGroups() {
        var invocations = fakeContext.getLlmInvocations();
        assertFalse(invocations.isEmpty(), "Expected at least one LLM invocation");
        return invocations.getFirst().getInteraction().getToolGroups();
    }

    /** A small sample of guide sections covering known anchors. */
    private GuideSections sampleSections() {
        return new GuideSections(List.of(
                new GuideSection(
                        "3.2.3. Blackboard",
                        "blackboard",
                        "Shared memory store for domain objects during an agent process.",
                        true
                ),
                new GuideSection(
                        "3.3. Goals, Actions and Conditions",
                        "reference.steps",
                        "Defines the building blocks of agent behaviour.",
                        true
                ),
                new GuideSection(
                        "3.9. Tools",
                        "reference.tools",
                        "In-process and MCP tool integration.",
                        true
                )
        ), true);
    }

    /** A minimal valid {@link ValidationReport} suitable for use as a fake LLM response. */
    private ValidationReport sampleReport(GuideSection section) {
        return new ValidationReport(
                section.title(),
                true,
                "The section is accurate and covers the topic adequately."
        );
    }
}
