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
 * A curated news digest produced by {@link NewsDigestAgent}, containing a list of
 * {@link DigestItem} entries and a short editorial narrative.
 *
 * <p>Implements {@link HasContent} so the digest can be consumed by downstream agents
 * or exported as a content asset.
 */
@JsonClassDescription("A curated news digest for a given topic")
public record NewsDigest(
        /** The topic that was researched. */
        String topic,
        /** The curated list of news items. */
        List<DigestItem> items,
        String content
) implements HasContent {

    @Override
    public String getContent() {
        return content();
    }
}
