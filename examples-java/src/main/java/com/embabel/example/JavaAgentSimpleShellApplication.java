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

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.config.annotation.LoggingThemes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot application that provides an interactive command-line shell for Embabel agents
 * with Star Wars themed logging.
 * <br>
 * This is a simple example application demonstrating how to set up an Embabel agent
 * environment with a customized logging theme.
 * <br>
 * Docker Tools are not enabled in this example to keep the setup straightforward,
 * but keep in mind that some examples may require Docker Tools for full functionality.
 *
 * <p>This application runs in interactive shell mode, allowing developers to test and interact
 * with agents through a REPL-like interface.
 *
 * @author Embabel Team
 * @see EnableAgents
 * @since 1.0
 */
@SpringBootApplication
@ConfigurationPropertiesScan(
        basePackages = {
                "com.embabel.example"
        }
)
@EnableAgents(
        loggingTheme = LoggingThemes.SEVERANCE
)
public class JavaAgentSimpleShellApplication {

    /**
     * Application entry point.
     *
     * <p>Starts the Spring Boot application with an interactive shell interface,
     * Star Wars themed logging.
     *
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(JavaAgentSimpleShellApplication.class, args);
    }
}
