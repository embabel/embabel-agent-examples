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
package com.embabel.example.common

import com.embabel.common.ai.model.Thinking

/**
 * Reasoning effort for the examples' calls.
 *
 * Claude 4.7 and later models think adaptively, and Embabel maps a thinking token budget to their
 * effort level: under 1024 is low, under 4096 is medium. On those models the budget is not a token cap.
 * Older Claude models receive it as a literal thinking budget instead.
 *
 * Effort is set per call because the configured default model carries no effort of its own.
 */
object Effort {

    @JvmField
    val LOW: Thinking = Thinking.withTokenBudget(512)

    @JvmField
    val MEDIUM: Thinking = Thinking.withTokenBudget(1024)
}
