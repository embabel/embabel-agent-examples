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
 * A single news item within a [NewsDigest], carrying a headline, summary, and source URL.
 */
@JsonClassDescription("A single news item in a digest")
data class DigestItem(
    @get:JsonPropertyDescription("Headline of the news item")
    val headline: String,
    @get:JsonPropertyDescription("Brief summary of the news item")
    val summary: String,
    @get:JsonPropertyDescription("Source URL")
    val url: String,
)

/**
 * Container for a list of [DigestItem] values.
 *
 * Wraps `List<DigestItem>` as a named type to avoid generic type erasure during
 * LLM-driven JSON deserialisation. Using `createObject<List<DigestItem>>` loses
 * the type parameter at runtime and Jackson returns `List<LinkedHashMap>`;
 * wrapping in a named class preserves the element type.
 */
@JsonClassDescription("A list of news digest items")
data class DigestItemList(
    @get:JsonPropertyDescription("The list of news items")
    val items: List<DigestItem>,
)

/**
 * A research topic extracted from freeform user input, optionally narrowed
 * to a specific focus area within that topic.
 */
@JsonClassDescription("A research topic extracted from user input")
data class NewsTopic(
    @get:JsonPropertyDescription("The topic to research")
    val topic: String,
    @get:JsonPropertyDescription("Optional focus area within the topic")
    val focusArea: String = "",
)

/**
 * A curated news digest produced by a news digest agent, containing a list of
 * [DigestItem] entries and a short editorial narrative.
 *
 * Implements [HasContent] so the digest can be consumed by downstream agents
 * or exported as a content asset.
 */
@JsonClassDescription("A curated news digest for a given topic")
data class NewsDigest(
    /** The topic that was researched. */
    val topic: String,
    /** The curated list of news items. */
    val items: List<DigestItem>,
    override val content: String,
) : HasContent
