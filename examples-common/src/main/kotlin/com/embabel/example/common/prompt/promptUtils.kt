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
package com.embabel.example.common.prompt

import com.embabel.common.util.DummyInstanceCreator
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper

/**
 * Utility functions for building prompts.
 *
 * Jackson 3 made [ObjectMapper] immutable (no more fluent `.registerModule(...)` on a constructed
 * mapper) and folded JSR-310 Java Time support into databind itself, so the previous
 * `jacksonObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())` chain
 * collapses to a single call: [jacksonObjectMapper] already wires up the Kotlin module.
 */
object PromptUtils {

    val dummyInstanceCreator = DummyInstanceCreator()

    val om: ObjectMapper = jacksonObjectMapper()

    /**
     * Generates a JSON example of the given class
     * with dummy data. Makes few shot examples easier to create.
     *
     * @param clazz The class to generate a JSON example for.
     */
    @JvmStatic
    fun jsonExampleOf(clazz: Class<*>): String {
        val dummy = dummyInstanceCreator.createDummyInstance(clazz)
        return om.writerWithDefaultPrettyPrinter().writeValueAsString(dummy)
    }

    /**
     * Generates a JSON example of the given class
     * with dummy data. Makes few shot examples easier to create.
     *
     * @param T The type to generate a JSON example for.
     */
    inline fun <reified T> jsonExampleOf(): String {
        return jsonExampleOf(T::class.java)
    }
}
