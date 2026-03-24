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
 * Agent that produces a structured market intelligence report for a given company or sector.
 *
 * The agent follows a three-step pipeline:
 * 1. [parseSubject] — extracts the analysis subject and region from freeform user input.
 * 2. [gatherIntelligence] — searches the web for recent news, competitor activity,
 *    and industry trends.
 * 3. [synthesiseReport] — runs four sequential LLM calls to produce a SWOT analysis,
 *    competitive insights, key trends, and an executive summary, then assembles
 *    the final [MarketIntelligenceReport].
 *
 * Access to this agent requires the `market:admin` authority, enforced at the
 * MCP tool level via `@SecureAgentTool`.
 *
 * Domain types used by this agent are defined in `MarketIntelligenceTypes.kt` in
 * `examples-common` and shared with the Java implementation.
 */
@Agent(
    description = "Produce a structured market intelligence report including SWOT analysis, " +
        "competitive landscape, and key trends for a given company or sector",
)
@SecureAgentTool("hasAuthority('market:admin')")
class MarketIntelligenceAgent {

    /**
     * Extracts the [AnalysisSubject] from freeform [UserInput].
     *
     * Uses the default LLM to identify the company name, sector, or market segment
     * and the geographic region. Defaults the region to `Global` when not specified.
     *
     * @param userInput the raw user request
     * @param context the current operation context
     * @return the parsed [AnalysisSubject]
     */
    @Action
    fun parseSubject(
        userInput: UserInput,
        context: OperationContext,
    ): AnalysisSubject = context.ai()
        .withDefaultLlm()
        .createObject(
            """
            Extract the subject of market analysis from the following user input.
            Identify the company name, sector, or market segment, and the geographic region if mentioned.
            Default the region to Global if not specified.

            User input: ${userInput.content}
            """.trimIndent()
        )

    /**
     * Gathers raw market intelligence for the given [AnalysisSubject] via web search.
     *
     * Searches for recent news and developments (last 90 days), key competitors
     * and their recent moves, industry trends and disruptions, and notable
     * strengths or vulnerabilities. Returns a detailed summary of at least 400 words.
     *
     * @param subject the analysis subject and region
     * @param context the current operation context
     * @return a raw intelligence summary string
     */
    @Action
    fun gatherIntelligence(
        subject: AnalysisSubject,
        context: OperationContext,
    ): String = context.ai()
        .withDefaultLlm()
        .withToolGroup(CoreToolGroups.WEB)
        .createObject(
            """
            Search the web to gather recent market intelligence about: ${subject.subject}
            Region: ${subject.region}

            Collect the following in a structured summary:
            - Recent news and developments (last 90 days)
            - Key competitors and their recent moves
            - Industry trends and disruptions
            - Any notable strengths or vulnerabilities

            Return a detailed raw intelligence summary of at least 400 words.
            """.trimIndent()
        )

    /**
     * Synthesises a [MarketIntelligenceReport] from the gathered intelligence.
     *
     * Runs four sequential LLM calls against the raw intelligence to produce:
     * - A [SwotEntryList] with 2–3 items per category.
     * - A [CompetitorInsightList] covering the top 4 competitors.
     * - A [KeyTrendList] of the 5 most significant market trends.
     * - A four-sentence executive summary.
     *
     * The results are assembled into the final [MarketIntelligenceReport] and
     * exported remotely as `marketIntelligenceReport`.
     *
     * @param subject the analysis subject and region
     * @param rawIntelligence the raw intelligence summary from [gatherIntelligence]
     * @param context the current operation context
     * @return the completed [MarketIntelligenceReport]
     */
    @AchievesGoal(
        description = "Produce a structured market intelligence report with SWOT analysis, " +
            "competitive insights, and key trends",
        export = Export(
            remote = true,
            name = "marketIntelligenceReport",
            startingInputTypes = [UserInput::class],
        )
    )
    @Action
    fun synthesiseReport(
        subject: AnalysisSubject,
        rawIntelligence: String,
        context: OperationContext,
    ): MarketIntelligenceReport {
        val swotList = context.ai()
            .withDefaultLlm()
            .createObject<SwotEntryList>(
                """
                Based on the following market intelligence, produce a SWOT analysis for ${subject.subject}.
                Each entry must have a category (Strength, Weakness, Opportunity, or Threat)
                and a concise description. Return 2-3 items per category.

                Intelligence:
                $rawIntelligence
                """.trimIndent()
            )

        val competitorList = context.ai()
            .withDefaultLlm()
            .createObject<CompetitorInsightList>(
                """
                Based on the following market intelligence, identify the top 4 competitors
                or comparable entities for ${subject.subject} in ${subject.region}.
                For each, provide a notable recent development or strategic positioning insight.

                Intelligence:
                $rawIntelligence
                """.trimIndent()
            )

        val trendList = context.ai()
            .withDefaultLlm()
            .createObject<KeyTrendList>(
                """
                Based on the following market intelligence, extract the 5 most significant
                market trends affecting ${subject.subject} in ${subject.region}.
                Return each as a concise single sentence inside the items list.

                Intelligence:
                $rawIntelligence
                """.trimIndent()
            )

        val summary = context.ai()
            .withDefaultLlm()
            .createObject<String>(
                """
                Write a 4-sentence executive summary for a market intelligence report on
                ${subject.subject} (${subject.region}).
                Be concise, analytical, and suitable for a senior decision-maker.

                Key findings:
                SWOT: ${swotList.items.joinToString("; ") { "${it.category}: ${it.description}" }}
                Trends: ${trendList.items.joinToString("; ")}
                """.trimIndent()
            )

        return MarketIntelligenceReport(
            subject = subject.subject,
            region = subject.region,
            executiveSummary = summary,
            swot = swotList.items,
            competitors = competitorList.items,
            keyTrends = trendList.items,
            content = summary,
        )
    }
}
