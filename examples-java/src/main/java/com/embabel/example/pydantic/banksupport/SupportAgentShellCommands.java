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
