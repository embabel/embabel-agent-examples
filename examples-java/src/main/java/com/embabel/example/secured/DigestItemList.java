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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Container for a list of {@link DigestItem} values.
 *
 * <p>Wraps {@code List<DigestItem>} as a named type to avoid generic type erasure during
 * LLM-driven JSON deserialisation. Using {@code createObject} with a raw {@code List} type
 * loses the element type at runtime and Jackson returns {@code List<LinkedHashMap>};
 * wrapping in a named record preserves the element type.
 */
@JsonClassDescription("A list of news digest items")
public record DigestItemList(
        @JsonPropertyDescription("The list of news items")
        List<DigestItem> items
) {
}
