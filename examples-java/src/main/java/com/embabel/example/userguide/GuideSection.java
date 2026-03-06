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

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * A single section of the Embabel User Guide that is relevant to a query.
 *
 * @param title      human-readable section heading (e.g. "3.2 Agent Process Flow")
 * @param anchor     anchor fragment as it appears in the guide URL (e.g. "agent-process-flow")
 * @param summary    one-paragraph précis of what the section covers
 * @param hasContent always true — sections in the known guide list are confirmed to have content
 */
record GuideSection(
        String title,
        String anchor,
        @JsonPropertyDescription("One-paragraph summary of what this section covers")
        String summary,
        @JsonPropertyDescription(
                "always set to true — sections in the known guide list are confirmed to have content")
        boolean hasContent
) {
}
