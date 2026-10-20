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


import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.api.models.OpenAiModels;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.repository.Repository;
import org.springframework.lang.Nullable;

record Customer(Long id, String name, float balance, float pendingAmount) {

    @Tool(description = "Find the balance of a customer by id")
    float balance(boolean includePending) {
        return includePending ? balance + pendingAmount : balance;
    }
}

interface CustomerRepository extends Repository<Customer, Long> {

    @Nullable
    Customer findById(Long id);
}

record SupportInput(
        @JsonPropertyDescription("Customer ID") Long customerId,
        @JsonPropertyDescription("Query from the customer") String query) {
}

record SupportOutput(
        @JsonPropertyDescription("Advice returned to the customer") String advice,
        @JsonPropertyDescription("Recommendation only: true if the card should be blocked. Setting this does not block the card.") boolean blockCard,
        @JsonPropertyDescription("Risk level of query") int risk) {
}

@Agent(description = "Customer support agent")
record SupportAgent(CustomerRepository customerRepository) {

    @AchievesGoal(description = "Help bank customer with their query")
    @Action
    SupportOutput supportCustomer(SupportInput supportInput, OperationContext context) {
        var customer = customerRepository.findById(supportInput.customerId());
        if (customer == null) {
            return new SupportOutput("Customer not found with this id", false, 0);
        }
        return context.ai()
                .withLlm(OpenAiModels.GPT_41_MINI)
                .withToolObject(customer)
                .createObject(
                        """
                                You are a support agent in our bank, give the
                                customer support and judge the risk level of their query.
                                You can only look up their balance. You cannot block cards or take any other action on the account.
                                If their card may be lost, stolen or misused, set blockCard to true and explain why.
                                In that case, recommend that they block it now in the bank's app or by calling the bank's card support line.
                                Never say that you have blocked, are blocking, or have marked their card to be blocked.
                                Reply using the customer's name, "%s".
                                Currencies are in $.
                                
                                Their query: [%s]
                                """.formatted(customer.name(), supportInput.query()),
                        SupportOutput.class);
    }

}
