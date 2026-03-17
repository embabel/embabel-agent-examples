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
package com.embabel.example.support;

import com.embabel.agent.api.annotation.LlmTool;
import com.embabel.agent.api.tool.ToolCallContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Diagnostic tools that verify {@link ToolCallContext} propagation end-to-end.
 * <p>
 * When added to a tool group (e.g., WEB), the LLM can invoke these tools during
 * an agent run. The {@code checkContext} method receives {@link ToolCallContext}
 * via framework injection (just like Spring AI injects {@code ToolContext} into
 * {@code @Tool} methods) and logs the entries it finds.
 * <p>
 * Usage from the shell:
 * <pre>
 *   set-context tenantId=acme,apiKey=secret123
 *   x "Lynda is a scorpio. Find news for her" -d
 * </pre>
 * Look for log output: {@code ContextDiagnosticTools - ToolCallContext received: {tenantId=acme, apiKey=secret123}}
 */
public class ContextDiagnosticTools {

    private static final Logger logger = LoggerFactory.getLogger(ContextDiagnosticTools.class);

    @LlmTool(
            name = "check_tool_call_context",
            description = "Diagnostic tool: checks what context metadata (auth tokens, tenant IDs, etc.) " +
                    "is available to tools in this execution. Call this tool first before performing any " +
                    "web searches to verify the execution environment.",
            returnDirect = false
    )
    public String checkContext(
            @LlmTool.Param(description = "Brief reason for the context check", required = false) String reason,
            ToolCallContext context
    ) {
        var contextMap = context.toMap();

        if (contextMap.isEmpty()) {
            logger.warn("ToolCallContext received: EMPTY — no context was propagated");
            return "Context check complete. No tool call context entries found. " +
                    "This means no context was set via ProcessOptions.withToolCallContext().";
        }

        logger.info("ToolCallContext received: {}", contextMap);

        // Log individual entries for easy grep
        for (var entry : contextMap.entrySet()) {
            logger.info("  context[{}] = {}", entry.getKey(), entry.getValue());
        }

        var sb = new StringBuilder();
        sb.append("Context check complete. Found ").append(contextMap.size()).append(" entries:\n");
        for (var entry : contextMap.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
