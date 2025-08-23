package com.embabel.example.injection;

import com.embabel.agent.api.common.OperationContext;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.stereotype.Component;


/**
 * Demonstrate the simplest use of Embabel's AI capabilities,
 * injecting an AI OperationContext into a Spring component.
 * The jokes will be terrible, but don't blame Embabel, blame the LLM.
 *
 * @param operationContext
 */
@Component
public record InjectedComponent(
        OperationContext operationContext
) {

    public record Joke(String leadup, String punchline) {
    }

    public String tellJokeAbout(String topic) {
        return operationContext.ai()
                .withLlm(LlmOptions.withDefaultLlm().withTemperature(.8))
                .generateText("Tell me a joke about " + topic);
    }

    public Joke createJokeObjectAbout(String topic1, String topic2, String voice) {
        return operationContext.ai()
                .withLlm(LlmOptions.withDefaultLlm().withTemperature(.8))
                .createObject("""
                                Tell me a joke about %s and %s.
                                The voice of the joke should be %s.
                                The joke should have a leadup and a punchline.
                                """.formatted(topic1, topic2, voice),
                        Joke.class);
    }

}
