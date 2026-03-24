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
package com.embabel.example

import com.embabel.example.common.support.McpServers
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Spring Boot application that runs the secured Embabel agent MCP server.
 *
 * Extends the base MCP server with two additional security layers:
 *
 *
 * ### Agents exposed
 *
 * | Agent | Required authority |
 * |---|---|
 * | `NewsDigestAgent` | `news:read` |
 * | `MarketIntelligenceAgent` | `market:admin` |
 *
 *
 * ### OAuth2 configuration
 *
 * Configure your issuer URI in `examples-common/application-secured.yml`:
 * ```yaml
 * spring:
 *   security:
 *     oauth2:
 *       resourceserver:
 *         jwt:
 *           issuer-uri: https://your-idp.example.com
 * ```
 *
 * MCP clients must pass a signed JWT carrying the required authorities:
 * ```
 * Authorization: Bearer <signed-jwt-with-required-authorities>
 * ```
 * Use `McpSyncHttpClientRequestCustomizer` on the client side to attach the header.
 *
 * @see com.embabel.agent.config.mcpserver.security.SecuredAgentSecurityConfiguration
 * @see com.embabel.agent.config.mcpserver.security.SecureAgentToolConfiguration
 */
@SpringBootApplication
@ConfigurationPropertiesScan(
    basePackages = ["com.embabel.example"]
)
class KotlinAgentSecuredMcpServerApplication

/**
 * Application entry point.
 *
 * Activates the [McpServers.DOCKER], [McpServers.DOCKER_DESKTOP], and `secured` profiles,
 * enabling the Docker-based MCP transport and JWT security configuration.
 *
 * @param args command-line arguments forwarded to Spring Boot
 */
fun main(args: Array<String>) {
    runApplication<KotlinAgentSecuredMcpServerApplication>(*args) {
        setAdditionalProfiles(
            McpServers.DOCKER,
            McpServers.DOCKER_DESKTOP,
            McpServers.SECURED_PROFILE,
        )
    }
}
