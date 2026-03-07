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

/**
 * Represents a user's query or topic of interest within the Embabel User Guide.
 * Acts as the starting input for the {@link UserGuideValidatorAgent}.
 *
 * <p>Think of this like a research ticket: the user names a concept or concern
 * (e.g. "Actions and Goals", "Blackboard", "RAG integration"), and the agent
 * will locate, surface, and validate the corresponding guide content.
 *
 * @param topic     the concept, feature, or section the user wants to explore or validate
 * @param checkOnly when {@code true}, only check whether the section exists in the guide
 *                  and skip content validation entirely; when {@code false} (default),
 *                  perform full content validation
 */
public record UserGuideQuery(
        String topic,
        boolean checkOnly
) {
    /**
     * Convenience constructor — full validation, the common case.
     */
    public UserGuideQuery(String topic) {
        this(topic, false);
    }
}