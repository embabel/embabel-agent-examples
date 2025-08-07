package com.embabel.example.repeatuntil;

import com.embabel.agent.api.common.workflow.TextFeedback;
import com.embabel.agent.core.Agent;
import com.embabel.agent.experimental.api.common.workflow.RepeatUntilBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

record Goat(String name) {
}

/**
 * Show how a bean definition will be picked up as an agent.
 */
@Configuration
public class GoatWriterConfig {

    @Bean
    public Agent goatWriter() {
        return RepeatUntilBuilder
                .returning(Goat.class)
                .withMaxIterations(3)
                .repeating(context -> {
                    var lastFeedback = context.getInput().lastFeedback();
                    var feedback = lastFeedback != null ? "Feedback: " + lastFeedback.getFeedback() : "";
                    return context.promptRunner().createObject(
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
