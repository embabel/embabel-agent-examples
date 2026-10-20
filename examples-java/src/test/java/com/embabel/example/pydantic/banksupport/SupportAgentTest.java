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
package com.embabel.example.pydantic.banksupport;

import com.embabel.agent.test.unit.FakeOperationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SupportAgentTest {

    @Test
    void promptStatesTheAgentCanOnlyRecommendBlockingTheCard() {
        var agent = new SupportAgent(new InMemoryCustomerRepository());
        var context = new FakeOperationContext();
        context.expectResponse(new SupportOutput("I recommend blocking your card", true, 9));

        agent.supportCustomer(new SupportInput(123L, "I think my card was stolen"), context);

        var prompt = context.getLlmInvocations().getFirst().getMessages().getFirst().getContent();
        assertTrue(prompt.contains("John"));
        assertTrue(prompt.contains("I think my card was stolen"));
        assertTrue(prompt.contains("You cannot block cards"), prompt);
        assertTrue(prompt.contains("recommend that they block it"), prompt);
        assertTrue(prompt.contains("Never say that you have blocked"), prompt);
    }

    @Test
    void unknownCustomerNeedsNoLlmCall() {
        var agent = new SupportAgent(new InMemoryCustomerRepository());
        var context = new FakeOperationContext();

        var output = agent.supportCustomer(new SupportInput(999L, "What's my balance?"), context);

        assertEquals("Customer not found with this id", output.advice());
        assertTrue(context.getLlmInvocations().isEmpty());
    }
}
