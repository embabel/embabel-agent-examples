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
package com.embabel.example.handoff;

import com.embabel.agent.api.annotation.*;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.example.horoscope.StarPerson;
import com.embabel.example.horoscope.Writeup;

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
            export = @Export(name = "chooseAdventure", remote = true, startingInputTypes = {UserInput.class}))
    AdventureLog chooseAdventure(Choice choice, OperationContext operationContext) {
        return operationContext.ai()
                .withAutoLlm()
                .withHandoffs(StarPerson.class, Writeup.class)
                .createObject("""
                        Based on the user's input, decide what to do. You might do something related to a horoscope or some fact checking.
                        What they said: %s
                        """.formatted(choice.choice()), AdventureLog.class);
    }
}
