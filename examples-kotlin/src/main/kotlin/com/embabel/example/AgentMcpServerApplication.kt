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
package com.embabel.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.embabel.agent.config.annotation.EnableAgentMcpServer
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.McpServers
import io.modelcontextprotocol.server.McpServer

/**
 * Spring Boot application that runs Embabel agents as an MCP (Model Context Protocol) server.
 *
 * This application exposes agents as MCP-compatible tools that can be consumed by
 * AI assistants like Claude Desktop, IDEs, or other MCP clients. The server
 * implements the JSON-RPC based MCP protocol for tool discovery and execution.
 *
 * @see EnableAgentMcpServer
 */
@SpringBootApplication
@EnableAgentMcpServer
@EnableAgents(
    mcpServers = [McpServers.DOCKER_DESKTOP, McpServers.DOCKER],
)
class AgentMcpServerApplication

/**
 * Application entry point that starts the MCP server.
 *
 * Initializes Spring Boot with MCP server configuration and begins
 * listening for JSON-RPC requests from MCP clients.
 *
 * @param args Command line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<AgentMcpServerApplication>(*args)
}
