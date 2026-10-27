/*
 * Copyright 2024-2026 Embabel Pty Ltd.
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
package com.embabel.example.companynamer;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.annotation.Export;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;

import java.util.List;

record CompanyNameIdea(String name, String domain, String rationale) {
}

record CompanyNameIdeas(List<CompanyNameIdea> ideas) {
}

record CheckedCompanyName(String name, String rationale, DomainAvailability domainAvailability) {
}

record CompanyNamingReport(List<CheckedCompanyName> suggestions) {
}

/**
 * Suggests company names and performs quota-free DNS checks for their domains.
 */
@Agent(
        name = "JavaCompanyNamer",
        description = "Suggest company names and check whether their domain names have DNS records",
        beanName = "javaCompanyNamer")
public class CompanyNamer {

    private final DomainAvailabilityService domainAvailabilityService;

    public CompanyNamer(DomainAvailabilityService domainAvailabilityService) {
        this.domainAvailabilityService = domainAvailabilityService;
    }

    @Action
    CompanyNameIdeas suggestNames(UserInput userInput, Ai ai) {
        return ai.withDefaultLlm().createObject(
                """
                        Suggest 8 memorable company names for the following brief:
                        <brief>%s</brief>

                        For each suggestion, provide:
                        - the company name
                        - a concise rationale
                        - one matching fully qualified .com domain name, using only letters, digits, and hyphens

                        Make every company name and domain distinct. Domain availability will be checked separately,
                        so do not claim that a domain is available.
                        """.formatted(userInput.getContent()),
                CompanyNameIdeas.class);
    }

    @AchievesGoal(
            description = "Suggest company names with matching domain-name checks",
            export = @Export(
                    remote = true,
                    name = "nameCompanyJava",
                    startingInputTypes = {UserInput.class}))
    @Action
    CompanyNamingReport checkDomains(CompanyNameIdeas companyNameIdeas) {
        var checkedNames = companyNameIdeas.ideas().stream()
                .map(idea -> new CheckedCompanyName(
                        idea.name(),
                        idea.rationale(),
                        domainAvailabilityService.check(idea.domain())))
                .toList();
        return new CompanyNamingReport(checkedNames);
    }
}
