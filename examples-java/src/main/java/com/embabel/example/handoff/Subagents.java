package com.embabel.example.handoff;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.WaitFor;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.common.Subagent;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.common.ai.model.LlmOptions;
import com.embabel.example.horoscope.StarNewsFinder;
import com.embabel.example.horoscope.StarPerson;

import java.util.List;

record TargetPerson(String name, String sign) {
}

record InterestingFacts(List<String> facts) {
}

/**
 * Works with subagents to find interesting facts about a person based on their star sign.
 */
@Agent(description = "Find interesting things for this person")
class Subagents {

    @Action
    TargetPerson acquireTarget() {
        return WaitFor.formSubmission("Please enter the name and star sign of a person",
                TargetPerson.class);
    }

    @AchievesGoal(description = "Find interesting facts about a person")
    @Action
    InterestingFacts findInterestingFacts(TargetPerson person, OperationContext operationContext) {
        return operationContext.promptRunner()
                .withLlm(LlmOptions.withAutoLlm())
                .withSubagents(
                        new Subagent(StarNewsFinder.class, StarPerson.class),
                        new Subagent("GoatWriter", UserInput.class))
                .createObject("""
                                The person has a strong interest in astrology and goats.
                                Find 5 facts that will interest them, and return them as a list.
                                Each fact should be a limerick.
                                
                                Person: %s with star sign %s
                                """.formatted(person.name(), person.sign()),
                        InterestingFacts.class);
    }
}
