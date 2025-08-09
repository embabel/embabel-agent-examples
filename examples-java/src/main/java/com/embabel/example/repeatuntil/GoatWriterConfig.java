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
package com.embabel.example.repeatuntil;

import com.embabel.agent.api.common.workflow.loop.RepeatUntilAcceptableBuilder;
import com.embabel.agent.api.common.workflow.loop.TextFeedback;
import com.embabel.agent.config.models.OpenAiModels;
import com.embabel.agent.core.Agent;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

record Goat(String description) {
}

/**
 * Show how a bean definition will be picked up as an agent.
 */
@Configuration
public class GoatWriterConfig {

    @Bean
    public Agent goatWriter() {
        return RepeatUntilAcceptableBuilder
                .returning(Goat.class)
                .withMaxIterations(3)
                .repeating(context -> {
                    var lastFeedback = context.getInput().lastFeedback();
                    var feedback = lastFeedback != null ? "Feedback: " + lastFeedback.getFeedback() : "";
                    return context.promptRunner()
                            .withLlm(LlmOptions.withModel(OpenAiModels.GPT_5_NANO))
                            .createObject(
                                    """
                                            Describe a goat in 200 words. Be creative.
                                            
                                            %s
                                            """.formatted(feedback),
                                    Goat.class);
                })
                .withEvaluator(context ->
                        context.promptRunner().createObject(
                                """
                                        Evaluate the goat description for creativity and detail.
                                        The goat must have a name.
                                        Description:
                                        %s
                                        
                                        Provide feedback of no more than 40 words on a scale from 0 to 1, where 1 is excellent and 0 is poor.
                                        """.formatted(context.getInput().resultToEvaluate()),
                                TextFeedback.class))
                .buildAgent("GoatWriter",
                        "Write a description of a goat");
    }
}
