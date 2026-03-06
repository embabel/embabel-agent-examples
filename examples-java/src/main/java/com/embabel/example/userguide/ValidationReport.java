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
 * Concise validation result for an Embabel User Guide section.
 *
 * @param sectionTitle the guide section that was validated
 * @param passed       true if the section contains no factual errors or broken code;
 *                     false if it contains demonstrably wrong facts or non-existent API names
 * @param summary      one or two sentences stating the verdict — no improvement suggestions
 */
record ValidationReport(
        String sectionTitle,
        @JsonPropertyDescription(
                "true if the section is accurate and contains no factual errors or broken code; false otherwise")
        boolean passed,
        @JsonPropertyDescription(
                "One or two sentences stating the verdict. Do NOT include suggestions for improvement.")
        String summary
) {
}
