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

import com.embabel.agent.domain.library.HasContent;
import com.fasterxml.jackson.annotation.JsonClassDescription;

import java.util.List;

/**
 * A structured market intelligence report produced by {@link MarketIntelligenceAgent}.
 *
 * <p>Combines an executive summary, SWOT analysis, competitive landscape, and
 * key trend observations for a given subject and region.
 * Implements {@link HasContent} so the report can be consumed by downstream agents
 * or exported as a content asset.
 */
@JsonClassDescription("Market intelligence report")
public record MarketIntelligenceReport(
        /** Company name, sector, or market segment that was analysed. */
        String subject,
        /** Geographic scope of the report (e.g. {@code US}, {@code EU}, {@code Global}). */
        String region,
        /** Four-sentence executive summary suitable for a senior decision-maker. */
        String executiveSummary,
        /** SWOT analysis entries, 2–3 items per category. */
        List<SwotEntry> swot,
        /** Top competitors or comparable entities with strategic positioning notes. */
        List<CompetitorInsight> competitors,
        /** Five most significant market trends, each expressed as a single sentence. */
        List<String> keyTrends,
        String content
) implements HasContent {

    @Override
    public String getContent() {
        return content();
    }
}
