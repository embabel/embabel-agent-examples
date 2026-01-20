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
package com.embabel.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;


/**
 * Spring Boot application that provides an interactive command-line shell for Embabel agents
 * with Star Wars themed logging and Docker Desktop integration.
 *
 * <p>This application runs in Docker Container for the purpose of automated acceptance testing.
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
public class JavaA2ACotainerApplication {

    /**
     * Application entry point.
     *
     * <p>Starts the Spring Boot application with HTTP Listener and Google A2A Protocol.
     * <p>Note: Docker Tools are not supported yet. Need to figure out how to set them up in GitHub CI/CD pipeline
     * @param args command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JavaA2AServerApplication.class);
        app.run(args);
    }

}
