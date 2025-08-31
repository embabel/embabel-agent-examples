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
import com.embabel.agent.config.annotation.EnableAgents
import com.embabel.agent.config.annotation.LoggingThemes
import com.embabel.agent.config.annotation.McpServers

/**
 * Spring Boot application that runs Embabel agents in interactive shell mode.
 *
 * This application provides a command-line interface for testing and interacting
 * with agents. The shell allows executing agent commands, entering chat mode,
 * and debugging agent workflows.
 *
 * ## Example Usage
 * ```
 * shell:> execute "Find news for Alice who is a Gemini"
 * shell:> chat
 * shell:> help
 * ```
 *
 * @see EnableAgents
 */
@SpringBootApplication
@EnableAgents(
    loggingTheme = LoggingThemes.SEVERANCE,
    mcpServers = [McpServers.DOCKER_DESKTOP, McpServers.DOCKER],
)
class AgentShellMcpClientApplication

/**
 * Application entry point that bootstraps the Spring Boot application.
 *
 * Initializes the Spring context with agent auto-configuration and
 * starts the interactive shell interface.
 *
 * @param args Command line arguments passed to the application
 */
fun main(args: Array<String>) {
    runApplication<AgentShellMcpClientApplication>(*args)
}

