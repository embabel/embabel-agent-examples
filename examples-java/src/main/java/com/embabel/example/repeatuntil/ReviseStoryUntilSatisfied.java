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

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.agent.api.common.workflow.loop.RepeatUntilAcceptableBuilder;
import com.embabel.agent.api.common.workflow.loop.TextFeedback;
import com.embabel.agent.api.models.OpenAiModels;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;

record Story(String text) {
}

/**
 * Demonstrates a repeat-until agent that uses feedback
 */
@Agent(description = "Keep writing stories until the reviewer is satisfied")
class ReviseStoryUntilSatisfied {

    @AchievesGoal(description = "We have a story")
    @Action
    Story rewriteUntilSatisfied(
            UserInput userInput,
            ActionContext actionContext) {
        var writerPromptRunner = actionContext.ai()
                .withLlm(LlmOptions.withModel(OpenAiModels.GPT_41_MINI).withTemperature(.8));
        var reviewerPromptRunner = actionContext.ai()
                .withLlm(LlmOptions.withModel(OpenAiModels.GPT_41_MINI));
        return RepeatUntilAcceptableBuilder
                .returning(Story.class)
                .withMaxIterations(7)
                .withScoreThreshold(.8)
                .repeating(context -> {
                    var lastAttempt = context.lastAttempt();
                    var feedback = lastAttempt != null ? "Consider the following feedback: " + lastAttempt.getFeedback() : "";
                    return writerPromptRunner.createObject(
                            """
                                    Write a creative story inspired by the user input: %s
                                    
                                    %s
                                    """.formatted(userInput.getContent(), feedback),
                            Story.class
                    );
                })
                .withEvaluator(context ->
                        reviewerPromptRunner.createObject(
                                """
                                        Review and score the given story for creativity and relevance to the user input:
                                        Story:
                                        %s
                                        
                                        User input: %s
                                        """.formatted(
                                        context.getResultToEvaluate(),
                                        userInput.getContent()),
                                TextFeedback.class)
                )
                .build()
                .asSubProcess(actionContext, Story.class);

    }
}
