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
package com.embabel.example;

import com.embabel.example.common.support.McpServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot application that runs Embabel agents as an MCP (Model Context Protocol) server.
 *
 * <p>This application exposes your agents as MCP-compatible tools that can be consumed by
 * AI assistants like Claude Desktop, development environments with MCP support, or other
 * MCP-compliant clients. It also enables Docker Desktop integration for containerized
 * tool execution.
 *
 * @author Embabel Team
 * @since 1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan(
        basePackages = {
                "com.embabel.example"
        }
)
public class JavaMcpServerApplication {

    /**
     * Application entry point.
     *
     * <p>Starts the Spring Boot application with MCP server capabilities and
     * Docker Desktop integration enabled.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JavaAgentShellApplication.class);
        app.setAdditionalProfiles(McpServers.DOCKER_DESKTOP);
        app.run(args);
    }
}
