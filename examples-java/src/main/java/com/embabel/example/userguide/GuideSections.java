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

import java.util.List;

/**
 * A ranked list of guide sections relevant to a user query, plus a structural
 * integrity flag confirming the guide has the expected top-level shape.
 *
 * @param sections      sections ranked from most to least relevant
 * @param structureValid true if the guide contains at least 8 top-level chapters
 *                       AND an appendix section with content; false otherwise
 */
record GuideSections(
        List<GuideSection> sections,
        @JsonPropertyDescription(
                "true if the guide contains at least 8 top-level chapters AND an appendix section; false otherwise")
        boolean structureValid
) {
}
