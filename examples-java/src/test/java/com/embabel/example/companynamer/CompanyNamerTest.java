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

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.test.unit.FakeOperationContext;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyNamerTest {

    @Test
    void namingPromptContainsTheBriefAndDoesNotAssumeAvailability() {
        var context = new FakeOperationContext();
        context.expectResponse(new CompanyNameIdeas(List.of(
                new CompanyNameIdea("Bright Forge", "brightforge.com", "Memorable"))));
        var companyNamer = new CompanyNamer(new DomainAvailabilityService());

        companyNamer.suggestNames(new UserInput("A sustainable accounting platform"), context.ai());

        var prompt = context.getLlmInvocations().getFirst().getMessages().getFirst().getContent();
        assertTrue(prompt.contains("A sustainable accounting platform"));
        assertTrue(prompt.contains("do not claim that a domain is available"));
    }

    @Test
    void checksEverySuggestedDomain() {
        var checkedDomains = new java.util.ArrayList<String>();
        var service = new DomainAvailabilityService(domain -> {
            checkedDomains.add(domain);
            throw new UnknownHostException(domain);
        });
        var companyNamer = new CompanyNamer(service);
        var ideas = new CompanyNameIdeas(List.of(
                new CompanyNameIdea("Bright Forge", "brightforge.com", "Memorable"),
                new CompanyNameIdea("Green Ledger", "greenledger.com", "Descriptive")));

        var report = companyNamer.checkDomains(ideas);

        assertEquals(List.of("brightforge.com", "greenledger.com"), checkedDomains);
        assertEquals(2, report.suggestions().size());
    }
}
