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
package com.embabel.example.secured;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.mcpserver.security.SecureAgentTool;

import java.util.stream.Collectors;

/**
 * Agent that researches a topic via web search and returns a curated news digest
 * with headlines, summaries, and an editorial narrative.
 *
 * <p>The agent follows a two-step pipeline:
 * <ol>
 *   <li>{@link #extractTopic} — parses the user's freeform request into a structured
 *       {@link NewsTopic}.</li>
 *   <li>{@link #produceDigest} — searches the web for the five most relevant recent items,
 *       then generates a three-sentence editorial summary.</li>
 * </ol>
 *
 * <p>Access requires the {@code news:read} authority, enforced at the MCP tool level
 * via {@code @SecureAgentTool}.
 *
 * <p>Domain types used by this agent are defined in {@code NewsDigestTypes.kt} in
 * {@code examples-common} and shared with the Kotlin implementation.
 */
@Agent(description = "Research a topic and return a curated news digest with headlines and summaries")
@SecureAgentTool("hasAuthority('news:read')")
public class NewsDigestAgent {

    /**
     * Extracts a {@link NewsTopic} from freeform {@link UserInput}.
     *
     * <p>Uses the default LLM to identify the research topic and, if present,
     * a specific focus area within that topic.
     *
     * @param userInput the raw user request
     * @param context   the current operation context
     * @return the parsed {@link NewsTopic}
     */
    @Action
    public NewsTopic extractTopic(UserInput userInput, OperationContext context) {
        return context.ai()
                .withDefaultLlm()
                .createObject(
                        """
                        Extract a research topic from the following user input.
                        If a specific focus area is mentioned, capture it separately.

                        User input: %s
                        """.formatted(userInput.getContent()),
                        NewsTopic.class
                );
    }

    /**
     * Produces a {@link NewsDigest} for the given {@link NewsTopic}.
     *
     * <p>Performs two sequential LLM calls:
     * <ol>
     *   <li>A web-search-enabled call to retrieve the five most recent and relevant
     *       news items, each with a headline, 2–3 sentence summary, and source URL.</li>
     *   <li>A summarisation call to write a three-sentence editorial narrative
     *       across all retrieved items.</li>
     * </ol>
     *
     * <p>The results are assembled into the final {@link NewsDigest} and exported
     * remotely as {@code newsDigest}.
     *
     * @param topic   the structured topic produced by {@link #extractTopic}
     * @param context the current operation context
     * @return the completed {@link NewsDigest}
     */
    @AchievesGoal(
            description = "Produce a curated news digest for the requested topic",
            export = @Export(
                    remote = true,
                    name = "newsDigest",
                    startingInputTypes = {UserInput.class}
            )
    )
    @Action
    public NewsDigest produceDigest(NewsTopic topic, OperationContext context) {
        String focusClause = !topic.getFocusArea().isBlank()
                ? "Focus specifically on: " + topic.getFocusArea() + "."
                : "";

        DigestItemList digestItemList = context.ai()
                .withDefaultLlm()
                .withToolGroup(CoreToolGroups.WEB)
                .createObject(
                        """
                        Use web search to find the 5 most recent and relevant news items about: %s.
                        %s
                        For each item return a headline, a 2-3 sentence summary, and the source URL.
                        """.formatted(topic.getTopic(), focusClause),
                        DigestItemList.class
                );

        String narrative = context.ai()
                .withDefaultLlm()
                .createObject(
                        """
                        Write a 3-sentence editorial summary of these news items about %s.
                        Be concise and objective.

                        Items:
                        %s
                        """.formatted(
                                topic.getTopic(),
                                digestItemList.getItems().stream()
                                        .map(item -> "- " + item.getHeadline() + ": " + item.getSummary())
                                        .collect(Collectors.joining("\n"))
                        ),
                        String.class
                );

        return new NewsDigest(topic.getTopic(), digestItemList.getItems(), narrative);
    }
}
