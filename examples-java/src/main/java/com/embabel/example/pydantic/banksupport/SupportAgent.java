package com.embabel.example.pydantic.banksupport;


import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.embabel.agent.config.models.OpenAiModels;
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
        @JsonPropertyDescription("Whether to block their card or not") boolean blockCard,
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
                                In some cases, you may need to block their card. In this case, explain why.
                                Reply using the customer's name, "%s".
                                Currencies are in $.
                                
                                There query: [%s]
                                """.formatted(customer.name(), supportInput.query()),
                        SupportOutput.class);
    }

}
