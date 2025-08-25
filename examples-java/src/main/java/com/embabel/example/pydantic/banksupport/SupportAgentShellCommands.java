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
package com.embabel.example.pydantic.banksupport;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.embabel.agent.core.ProcessOptions;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public record SupportAgentShellCommands(
        AgentPlatform agentPlatform
) {

    @ShellMethod("Get bank support for a customer query")
    public String bankSupport(
            @ShellOption(value = "id", help = "customer id", defaultValue = "123") Long id,
            @ShellOption(value = "query", help = "customer query", defaultValue = "What's my balance, including pending amounts?") String query
    ) {
        var supportInput = new SupportInput(id, query);
        System.out.println("Support input: " + supportInput);
        var invocation = AgentInvocation
                .builder(agentPlatform)
                .options(ProcessOptions.builder().verbosity(v -> v.showPrompts(true)).build())
                .build(SupportOutput.class);
        var result = invocation.invoke(supportInput);
        return result.toString();
    }


}
