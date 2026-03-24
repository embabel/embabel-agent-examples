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

import com.embabel.example.common.support.McpServers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot application that runs the secured Embabel agent MCP server.
 *
 * <p>Extends the base MCP server with two additional security layers:
 * <ul>
 *   <li><b>HTTP layer:</b> JWT Bearer token required on all {@code /sse/**} and
 *       {@code /mcp/**} requests, enforced by {@code SecuredAgentSecurityConfiguration}
 *       with {@code spring-security-oauth2-resource-server}.</li>
 *   <li><b>Method layer:</b> {@code @SecureAgentTool} on each {@code @AchievesGoal} action
 *       enforces per-tool authority checks via {@code SecureAgentToolConfiguration} and
 *       {@code SecureAgentToolAspect}.</li>
 * </ul>
 *
 * <h2>Agents exposed</h2>
 * <table>
 *   <tr><th>Agent</th><th>Required authority</th></tr>
 *   <tr><td>{@code NewsDigestAgent}</td><td>{@code news:read}</td></tr>
 *   <tr><td>{@code MarketIntelligenceAgent}</td><td>{@code market:admin}</td></tr>
 * </table>
 *
 * <h2>OAuth2 configuration</h2>
 * <p>Configure your issuer URI in {@code application-secured.yml}:
 * <pre>{@code
 * spring:
 *   security:
 *     oauth2:
 *       resourceserver:
 *         jwt:
 *           issuer-uri: https://your-idp.example.com
 * }</pre>
 *
 * <p>MCP clients must pass a signed JWT carrying the required authorities:
 * <pre>{@code
 * Authorization: Bearer <signed-jwt-with-required-authorities>
 * }</pre>
 * Use {@code McpSyncHttpClientRequestCustomizer} on the client side to attach the header.
 *
 * @see com.embabel.agent.config.mcpserver.security.SecuredAgentSecurityConfiguration
 * @see com.embabel.agent.config.mcpserver.security.SecureAgentToolConfiguration
 */
@SpringBootApplication
@ConfigurationPropertiesScan(
        basePackages = {
                "com.embabel.example"
        }
)
public class JavaAgentSecuredMcpServerApplication {

    /**
     * Application entry point.
     *
     * <p>Activates the {@link McpServers#DOCKER}, {@link McpServers#DOCKER_DESKTOP},
     * and {@code secured} profiles, enabling the Docker-based MCP transport and
     * JWT security configuration.
     *
     * @param args command-line arguments forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JavaAgentSecuredMcpServerApplication.class);
        app.setAdditionalProfiles(
                McpServers.DOCKER,
                McpServers.DOCKER_DESKTOP,
                McpServers.SECURED_PROFILE
        );
        app.run(args);
    }
}
