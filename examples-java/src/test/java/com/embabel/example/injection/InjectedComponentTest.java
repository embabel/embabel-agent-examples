/*
 * Copyright 2024-2025 Embabel Software, Inc.
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
package com.embabel.example.injection;

import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.PromptRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit test for InjectedComponent.
 */
class InjectedComponentTest {

    @Test
    void testTellJokeAbout() {
        var mockAi = Mockito.mock(Ai.class);
        var mockPromptRunner = Mockito.mock(PromptRunner.class);

        var prompt = "Tell me a joke about frogs";
        // Yep, an LLM came up with this joke.
        var terribleJoke = """
                Why don't frogs ever pay for drinks?
                Because they always have a tadpole in their wallet!
                """;
        when(mockAi.withDefaultLlm()).thenReturn(mockPromptRunner);
        when(mockPromptRunner.generateText(prompt)).thenReturn(terribleJoke);

        var injectedComponent = new InjectedComponent(mockAi);
        var joke = injectedComponent.tellJokeAbout("frogs");

        assertEquals(terribleJoke, joke);
        Mockito.verify(mockAi).withDefaultLlm();
        Mockito.verify(mockPromptRunner).generateText(prompt);
    }

}