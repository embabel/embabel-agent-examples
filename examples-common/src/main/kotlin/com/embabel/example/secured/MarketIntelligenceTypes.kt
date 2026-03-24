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

import com.embabel.agent.domain.library.HasContent
import com.fasterxml.jackson.annotation.JsonClassDescription
import com.fasterxml.jackson.annotation.JsonPropertyDescription

/**
 * The subject of a market analysis request, capturing the entity or sector
 * to be analysed and the geographic scope of the report.
 */
@JsonClassDescription("Subject of market analysis")
data class AnalysisSubject(
    @get:JsonPropertyDescription("Company name, sector, or market segment to analyse")
    val subject: String,
    @get:JsonPropertyDescription("Geographic region of focus, e.g. US, EU, APAC, or Global")
    val region: String = "Global",
)

/**
 * A single observation about a competitor or comparable entity identified
 * during the competitive landscape analysis.
 */
@JsonClassDescription("Key player in the competitive landscape")
data class CompetitorInsight(
    @get:JsonPropertyDescription("Name of the competitor or comparable entity")
    val name: String,
    @get:JsonPropertyDescription("Notable recent development or positioning")
    val insight: String,
)

/**
 * Container for a list of [CompetitorInsight] items.
 *
 * Wraps `List<CompetitorInsight>` as a named type to avoid generic type erasure
 * during LLM-driven JSON deserialisation.
 */
@JsonClassDescription("A list of competitor insights")
data class CompetitorInsightList(
    val items: List<CompetitorInsight>,
)

/**
 * A single entry in a SWOT analysis, classifying an observation as a
 * Strength, Weakness, Opportunity, or Threat.
 */
@JsonClassDescription("A single SWOT observation")
data class SwotEntry(
    @get:JsonPropertyDescription("One of: Strength, Weakness, Opportunity, Threat")
    val category: String,
    @get:JsonPropertyDescription("Concise description of this SWOT item")
    val description: String,
)

/**
 * Container for a list of [SwotEntry] items.
 *
 * Wraps `List<SwotEntry>` as a named type to avoid generic type erasure
 * during LLM-driven JSON deserialisation.
 */
@JsonClassDescription("A list of SWOT entries")
data class SwotEntryList(
    val items: List<SwotEntry>,
)

/**
 * Container for a list of key market trend statements.
 *
 * Wraps `List<String>` as a named type to avoid generic type erasure
 * during LLM-driven JSON deserialisation.
 */
@JsonClassDescription("A list of key trend statements")
data class KeyTrendList(
    val items: List<String>,
)

/**
 * A structured market intelligence report, containing an executive summary,
 * SWOT analysis, competitive landscape, and key trend observations for a
 * given subject and region.
 *
 * Implements [HasContent] so the report can be consumed by downstream agents
 * or exported as a content asset.
 */
@JsonClassDescription("Market intelligence report")
data class MarketIntelligenceReport(
    /** Company name, sector, or market segment that was analysed. */
    val subject: String,
    /** Geographic scope of the report (e.g. `US`, `EU`, `Global`). */
    val region: String,
    /** Four-sentence executive summary suitable for a senior decision-maker. */
    val executiveSummary: String,
    /** SWOT analysis entries, 2–3 items per category. */
    val swot: List<SwotEntry>,
    /** Top competitors or comparable entities with strategic positioning notes. */
    val competitors: List<CompetitorInsight>,
    /** Five most significant market trends, each expressed as a single sentence. */
    val keyTrends: List<String>,
    override val content: String,
) : HasContent
