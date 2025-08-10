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
package com.embabel.example.factchecker;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.ActionContext;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.common.SupplierActionContext;
import com.embabel.agent.api.common.TransformationActionContext;
import com.embabel.agent.api.common.workflow.multimodel.ConsensusBuilder;
import com.embabel.agent.api.common.workflow.multimodel.ResultList;
import com.embabel.agent.config.models.OpenAiModels;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.domain.library.InternetResource;
import com.embabel.common.ai.model.LlmOptions;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

record FactualAssertion(String standaloneAssertion) {
}

record DistinctFactualAssertions(List<FactualAssertion> assertions) {
}

record FactCheck(
        String assertion,
        boolean isTrue,
        @JsonPropertyDescription("confidence in your judgment as to whether the assertion true or false. From 0-1")
        double confidence,
        @JsonPropertyDescription("reasoning for your scoring")
        String reasoning,
        List<InternetResource> links
) {

    List<InternetResource> getLinks() {
        return links;
    }
}

record FactChecks(
        List<FactCheck> checks
) {
}


@ConfigurationProperties("embabel.fact-checker")
record FactCheckerProperties(
        int reasoningWordCount,
        List<String> trustedSources,
        List<String> untrustedSources,
        List<String> models,
        String deduplicationModel,
        int maxConcurrency
) {
    FactCheckerProperties() {
        this(
                30,
                List.of(
                        "Wikipedia",
                        "Wikidata",
                        "Britannica",
                        "BBC",
                        "Reuters",
                        "ABC Australia"
                ),
                List.of(
                        "Reddit",
                        "4chan",
                        "Twitter"
                ),
                List.of(
                        OpenAiModels.GPT_41_MINI,
                        OpenAiModels.GPT_5_NANO,
                        OpenAiModels.GPT_5_MINI
                ),
                OpenAiModels.GPT_5_MINI,
                8
        );
    }

    List<LlmOptions> llms() {
        return models.stream()
                .map(LlmOptions::withModel)
                .map(buildable -> (LlmOptions) buildable)
                .toList();
    }

    LlmOptions deduplicationLlm() {
        return LlmOptions.withModel(deduplicationModel);
    }
}

@Agent(description = "Fact checker agent")
class FactChecker {

    private final FactCheckerProperties properties;

    public FactChecker(FactCheckerProperties properties) {
        this.properties = properties;
    }

    @Action
    DistinctFactualAssertions identifyDistinctFactualAssertions(UserInput userInput, ActionContext actionContext) {
        var generators = properties.llms().stream()
                .map(llm -> (Function<SupplierActionContext<DistinctFactualAssertions>, DistinctFactualAssertions>) context ->
                        generate(userInput, context, llm))
                .toList();
        return ConsensusBuilder
                .returning(DistinctFactualAssertions.class)
                .withSources(generators)
                .withConsensusBy(this::getDistinctFactualAssertions)
                .asSubProcess(actionContext);
    }


    @AchievesGoal(description = "Content has been fact-checked")
    @Action
    FactChecks factChecks(
            DistinctFactualAssertions distinctFactualAssertions,
            OperationContext context) {
        var promptRunner = context.ai().withLlm(
                        LlmOptions.withModel(OpenAiModels.GPT_41_MINI)
                )
                .withToolGroup(CoreToolGroups.WEB);
        var checks = context.parallelMap(distinctFactualAssertions.assertions(), properties.maxConcurrency(), assertion ->
                promptRunner.createObject(
                        """
                                Given the following assertion, check if it is true or false and explain why in %d words
                                Express your confidence in your determination as a number between 0 and 1.
                                Use web tools so you can cite information to support your conclusion.
                                
                                Be guided by the following regarding sources:
                                - Trusted sources: %s
                                - Untrusted sources: %s
                                
                                ASSERTION TO CHECK:
                                %s
                                """.formatted(
                                properties.reasoningWordCount(),
                                properties.trustedSources(),
                                properties.untrustedSources(),
                                assertion.standaloneAssertion()
                        ),
                        FactCheck.class
                ));
        return new FactChecks(checks);
    }

    private DistinctFactualAssertions generate(UserInput userInput, ActionContext context, LlmOptions llm) {
        return context.ai()
                .withLlm(llm)
                .createObject(
                        """
                                Extract distinct factual assertions from the following text.
                                The assertions may be incorrect; it is your job to identify them,
                                not to fact-check them.
                                
                                The assertions may overlap, so you should
                                return a list of distinct factual assertions, each expressed it at most %d words.
                                
                                TEXT TO ANALYZE FOLLOWS:
                                %s
                                """
                                .formatted(
                                        properties.reasoningWordCount(),
                                        userInput.getContent()),
                        DistinctFactualAssertions.class
                );
    }

    private DistinctFactualAssertions getDistinctFactualAssertions(
            TransformationActionContext<ResultList<DistinctFactualAssertions>, DistinctFactualAssertions> context) {
        var allAssertions = context.getInput().getResults().stream()
                .flatMap(result -> result.assertions().stream())
                .distinct()
                .toList();
        var assertionsContent = allAssertions.stream()
                .map(FactualAssertion::standaloneAssertion)
                .collect(Collectors.joining("\n"));
        return context.ai().withLlm(
                properties.deduplicationLlm()
        ).createObject(
                """
                        Your role is to consolidate different factual assertions into a single list,
                        with no overlap.
                        Each assertion you return should be clear and stand by itself.
                        
                        For example, if the input is:
                        
                        - "The sky is blue."
                        - "The sky is blue and the grass is green."
                        - "The grass is green."
                        You should return:
                        - "The sky is blue."
                        - "The grass is green."
                        
                        If the input is:
                        - France is larger than Sweden
                        - The user suggested that France is larger than Sweden
                        - Check whether France is larger than Sweden
                        You should return:
                        - "France is larger than Sweden."
                        
                        
                        Consolidate the following potentially overlapping factual assertions into a single list.
                        Each assertion should be expressed in at most %d words.
                        
                        TEXT TO CONSIDER:
                        %s
                        """
                        .formatted(properties.reasoningWordCount(), assertionsContent),
                DistinctFactualAssertions.class
        );
    }


}