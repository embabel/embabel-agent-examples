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
