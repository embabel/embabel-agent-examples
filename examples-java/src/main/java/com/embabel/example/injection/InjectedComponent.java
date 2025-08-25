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
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.stereotype.Component;


/**
 * Demonstrate the simplest use of Embabel's AI capabilities,
 * injecting an AI helper into a Spring component.
 * The jokes will be terrible, but don't blame Embabel, blame the LLM.
 *
 * @param ai the AI helper injected by Embabel
 */
@Component
public record InjectedComponent(Ai ai) {

    public record Joke(String leadup, String punchline) {
    }

    public String tellJokeAbout(String topic) {
        return ai
                .withDefaultLlm()
                .generateText("Tell me a joke about " + topic);
    }

    public Joke createJokeObjectAbout(String topic1, String topic2, String voice) {
        return ai
                .withLlm(LlmOptions.withDefaultLlm().withTemperature(.8))
                .createObject("""
                                Tell me a joke about %s and %s.
                                The voice of the joke should be %s.
                                The joke should have a leadup and a punchline.
                                """.formatted(topic1, topic2, voice),
                        Joke.class);
    }

}
