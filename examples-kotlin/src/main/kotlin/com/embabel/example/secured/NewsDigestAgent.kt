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
package com.embabel.example.secured

import com.embabel.agent.api.annotation.AchievesGoal
import com.embabel.agent.api.annotation.Action
import com.embabel.agent.api.annotation.Agent
import com.embabel.agent.api.annotation.Export
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.api.common.createObject
import com.embabel.agent.core.CoreToolGroups
import com.embabel.agent.domain.io.UserInput
import com.embabel.agent.mcpserver.security.SecureAgentTool

/**
 * Agent that researches a topic via web search and returns a curated news digest
 * with headlines, summaries, and an editorial narrative.
 *
 * The agent follows a two-step pipeline:
 * 1. [extractTopic] — parses the user's freeform request into a structured [NewsTopic].
 * 2. [produceDigest] — searches the web for the five most relevant recent items,
 *    then generates a three-sentence editorial summary.
 *
 * Access requires the `news:read` authority, enforced at the MCP tool level
 * via `@SecureAgentTool`.
 *
 * Domain types used by this agent are defined in `NewsDigestTypes.kt` in `examples-common`
 * and shared with the Java implementation.
 */
@Agent(
    description = "Research a topic and return a curated news digest with headlines and summaries",
)
@SecureAgentTool("hasAuthority('news:read')")
class NewsDigestAgent {

    /**
     * Extracts a [NewsTopic] from freeform [UserInput].
     *
     * Uses the default LLM to identify the research topic and, if present,
     * a specific focus area within that topic.
     *
     * @param userInput the raw user request
     * @param context the current operation context
     * @return the parsed [NewsTopic]
     */
    @Action
    fun extractTopic(
        userInput: UserInput,
        context: OperationContext,
    ): NewsTopic = context.ai()
        .withDefaultLlm()
        .createObject(
            """
            Extract a research topic from the following user input.
            If a specific focus area is mentioned, capture it separately.

            User input: ${userInput.content}
            """.trimIndent()
        )

    /**
     * Produces a [NewsDigest] for the given [NewsTopic].
     *
     * Performs two sequential LLM calls:
     * 1. A web-search-enabled call to retrieve the five most recent and relevant
     *    news items, each with a headline, 2–3 sentence summary, and source URL.
     * 2. A summarisation call to write a three-sentence editorial narrative
     *    across all retrieved items.
     *
     * The results are assembled into the final [NewsDigest] and exported remotely
     * as `newsDigest`.
     *
     * @param topic the structured topic produced by [extractTopic]
     * @param context the current operation context
     * @return the completed [NewsDigest]
     */
    @AchievesGoal(
        description = "Produce a curated news digest for the requested topic",
        export = Export(
            remote = true,
            name = "newsDigest",
            startingInputTypes = [UserInput::class],
        )
    )
    @Action
    fun produceDigest(
        topic: NewsTopic,
        context: OperationContext,
    ): NewsDigest {
        val focusClause = if (topic.focusArea.isNotBlank())
            "Focus specifically on: ${topic.focusArea}."
        else ""

        val digestItemList = context.ai()
            .withDefaultLlm()
            .withToolGroup(CoreToolGroups.WEB)
            .createObject<DigestItemList>(
                """
                Use web search to find the 5 most recent and relevant news items about: ${topic.topic}.
                $focusClause
                For each item return a headline, a 2-3 sentence summary, and the source URL.
                """.trimIndent()
            )

        val narrative = context.ai()
            .withDefaultLlm()
            .createObject<String>(
                """
                Write a 3-sentence editorial summary of these news items about ${topic.topic}.
                Be concise and objective.

                Items:
                ${digestItemList.items.joinToString("\n") { "- ${it.headline}: ${it.summary}" }}
                """.trimIndent()
            )

        return NewsDigest(
            topic = topic.topic,
            items = digestItemList.items,
            content = narrative,
        )
    }
}
