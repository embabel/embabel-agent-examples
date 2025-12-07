package com.embabel.example.supervisor;

import com.embabel.agent.api.common.scope.AgentScopeBuilder;
import com.embabel.agent.api.invocation.SupervisorInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.domain.io.UserInput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public record SupervisorShell(
        AgentPlatform agentPlatform,
        Stages stages
) {

    @ShellMethod("let's cook")
    public String cook(@ShellOption(defaultValue = "Steak tartare for Rod--ask Jamie to cook") String request) {
        var meal = SupervisorInvocation.on(agentPlatform)
                .returning(Stages.Meal.class)
                .withScope(AgentScopeBuilder.fromInstance(stages))
                .invoke(new UserInput(request));
        return meal.toString();
    }
}
