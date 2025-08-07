package com.embabel.example.repeatuntil;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.agent.api.common.workflow.TextFeedback;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.experimental.api.common.workflow.RepeatUntilBuilder;
import com.embabel.common.ai.model.LlmOptions;

record Story(String text) {
}

@Agent(description = "Keep writing stories until the reviewer is satisfied")
class RepeatUntilSatisfied {

    @AchievesGoal(description = "We have a story")
    @Action
    Story rewriteUntilSatisfied(
            UserInput userInput,
            ActionContext actionContext) {
        var writerPromptRunner = actionContext.promptRunner()
                .withLlm(LlmOptions.fromModel("gpt-4.1-nano").withTemperature(.8));
        var reviewerPromptRunner = actionContext.promptRunner()
                .withLlm(LlmOptions.fromModel("gpt-4.1-mini"));
        return RepeatUntilBuilder
                .returning(Story.class)
                .withMaxIterations(5)
                .withScoreThreshold(.8)
                .repeating(context ->
                        writerPromptRunner.createObject(
                                """
                                        Write a creative story inspired by the user input: %s
                                        """.formatted(userInput.getContent()),
                                Story.class
                        ))
                .withEvaluator(context ->
                        reviewerPromptRunner.createObject(
                                """
                                        Review and score the given story for creativity and relevance to the user input:
                                        Story:
                                        %s
                                        
                                        User input: %s
                                        """.formatted(
                                        context.getInput().resultToEvaluate(),
                                        userInput.getContent()),
                                TextFeedback.class)
                )
                .build()
                .asSubProcess(actionContext, Story.class);

    }
}
