package com.embabel.example.repeatuntil;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.agent.api.common.workflow.TextFeedback;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.experimental.api.common.workflow.RepeatUntilBuilder;
import com.embabel.agent.prompt.persona.Persona;
import com.embabel.common.ai.prompt.PromptContributionLocation;

abstract class Personas {
    static final Persona WRITER = Persona.create(
            "Roald Dahl",
            "A creative storyteller who loves to weave imaginative tales that are a bit unconventional",
            "Quirky",
            "Create memorable stories that captivate the reader's imagination.",
            "",
            PromptContributionLocation.BEGINNING
    );
    static final Persona REVIEWER = Persona.create(
            "Media Book Review",
            "New York Times Book Reviewer",
            "Professional and insightful",
            "Help guide readers toward good stories",
            "",
            PromptContributionLocation.BEGINNING
    );
}

record Story(String text) {
}

@Agent(description = "Keep writing text until the reviewer is satisfied")
class RepeatUntilSatisfied {

     @AchievesGoal(description="We have a story")
     @Action
     Story writeUntilSatisfied(UserInput userInput,
                               ActionContext actionContext) {
        return RepeatUntilBuilder
                .returning(Story.class)
                .repeating(context -> {
                    return new Story("thing");
                })
                .withEvaluator( context ->
                        new TextFeedback(1.0, "it's great"))
                .build()
                .asSubProcess(actionContext, Story.class);

    }
}
