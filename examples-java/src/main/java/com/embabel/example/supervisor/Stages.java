package com.embabel.example.supervisor;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.EmbabelComponent;
import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;

@EmbabelComponent
public class Stages {

    public record Cook(String name, int age) {
    }

    public record Order(String dish, int quantity) {
    }

    public record Meal(String dish, int quantity, String orderedBy, String cookedBy) {
    }

    @Action
    public Cook chooseCook(UserInput userInput, Ai ai) {
        return ai.withAutoLlm().createObject(
                """
                        From the following user input, choose a cook.
                        User input: %s
                        """.formatted(userInput),
                Cook.class
        );
    }

    @Action
    public Order takeOrder(UserInput userInput, Ai ai) {
        return ai.withAutoLlm().createObject(
                """
                        From the following user input, take a food order
                        User input: %s
                        """.formatted(userInput),
                Order.class
        );
    }

    @Action
    @AchievesGoal(description = "Cook the meal according to the order")
    public Meal prepareMeal(Cook cook, Order order, UserInput userInput, Ai ai) {
        // The model will get the orderedBy from UserInput
        return ai.withAutoLlm().createObject(
                """
                        Prepare a meal based on the cook and order details and target customer
                        Cook: %s, age %d
                        Order: %d x %s
                        User input: %s
                        """.formatted(cook.name(), cook.age(), order.quantity(), order.dish(), userInput.getContent()),
                Meal.class
        );
    }

}
