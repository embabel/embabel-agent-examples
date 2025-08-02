package com.embabel.example.handoff;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import com.embabel.example.horoscope.StarPerson;
import com.embabel.example.horoscope.Writeup;
import com.embabel.example.wikipedia.ResearchSubject;

record Choice(String choice) {

}

record AdventureLog(String log) {
}

@Agent(description = "Choose your own adventure handoff agent")
public class Handoff {

    /**
     * This action is get user input out of the way in selecting the adventure by handoff
     */
    @Action
    Choice makeChoice(UserInput userInput, OperationContext operationContext) {
        return WaitFor.formSubmission("Please choose your own adventure", Choice.class);
    }

    @Action
    @AchievesGoal(description = "Choose your own adventure",
        export = @Export(name="chooseAdventure", remote = true, startingInputTypes = {UserInput.class}))
    AdventureLog chooseAdventure(Choice choice, OperationContext operationContext) {
        return operationContext.promptRunner()
                .withLlm(LlmOptions.fromModel("gpt-4.1-nano"))
                .withHandoffs(StarPerson.class, Writeup.class)
                .createObject("""
                    Based on the user's input, decide what to do. You might do something related to a horoscope or some fact checking.
                    What they said: %s
                    """.formatted(choice.choice()), AdventureLog.class);
    }
}
