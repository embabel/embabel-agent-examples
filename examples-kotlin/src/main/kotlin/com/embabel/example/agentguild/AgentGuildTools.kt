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
package com.embabel.example.agentguild

import com.embabel.agent.api.tool.Tool
import com.embabel.agent.api.tool.TypedTool
import com.embabel.agent.core.ToolGroup
import com.embabel.agent.core.ToolGroupMetadata
import com.embabel.agent.core.ToolGroupPermission
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import tools.jackson.core.json.JsonFactory
import tools.jackson.core.StreamReadConstraints
import tools.jackson.core.StreamReadFeature
import tools.jackson.databind.JsonNode
import tools.jackson.databind.json.JsonMapper
import java.net.InetAddress
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.time.Clock
import java.time.Instant
import java.util.function.Function

private const val ORIGIN = "https://agent-guild-5d5r.onrender.com"
private const val BODY_LIMIT = 65_536
private const val INPUT_LIMIT = 131_072
private val checkNames = listOf("endpoint_reachable", "protocol_handshake", "agent_card_resolves", "agent_card_signed", "payment_claim_holds", "independent_evidence")

/** Only explicitly selected public input belongs in these arguments. No conversation or credentials. */
data class EndpointInput(
    @get:JsonPropertyDescription("Exact public HTTP(S) endpoint URL selected for observation, without credentials or fragment")
    val url: String,
)

data class PassportInput(
    @get:JsonPropertyDescription("Complete public Agent Guild passport JSON object as text; preserve every signed claim and proof")
    val credentialJson: String,
    @get:JsonPropertyDescription("Expected issuer did:key, obtained independently of this credential")
    val expectedIssuerDid: String,
    @get:JsonPropertyDescription("Expected counterparty subject did:key, obtained independently of this credential")
    val expectedSubjectDid: String,
    @get:JsonPropertyDescription("Maximum age in seconds accepted by the caller, from 1 to 86400")
    val maxAgeSeconds: Long,
)

data class GuildEvidence(
    val status: String,
    val code: String? = null,
    val target: String? = null,
    val observedAt: String? = null,
    val verdict: String? = null,
    val checks: Map<String, String>? = null,
    val failed: List<String>? = null,
    val unknowns: List<String>? = null,
    val verified: Boolean? = null,
    val issuerDid: String? = null,
    val subjectDid: String? = null,
    val validFrom: String? = null,
    val validUntil: String? = null,
)

private class Rejected(val code: String) : RuntimeException(code)
private fun requireEvidence(ok: Boolean, code: String = "invalid_input") {
    if (!ok) throw Rejected(code)
}

private val json = JsonMapper.builder(
    JsonFactory.builder()
        .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
        .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(32).maxNumberLength(1000).maxStringLength(INPUT_LIMIT).build())
        .build(),
).build()

private fun utf8(text: String, limit: Int): ByteArray {
    requireEvidence(text.length <= limit)
    val bytes = text.toByteArray(UTF_8)
    requireEvidence(bytes.size <= limit && String(bytes, UTF_8) == text)
    return bytes
}

private fun objectJson(text: String, limit: Int): JsonNode {
    utf8(text, limit)
    return json.createParser(text).use { parser ->
        val node = json.readTree(parser)
        requireEvidence(node != null && node.isObject && parser.nextToken() == null)
        node
    }
}

private fun JsonNode.text(name: String): String {
    val node = get(name)
    requireEvidence(node != null && node.isString)
    return node.asString()
}

private fun exactFields(node: JsonNode, names: Set<String>) {
    requireEvidence(node.properties().map { it.key }.toSet() == names)
}

private fun publicEndpoint(value: String) {
    utf8(value, 2048)
    requireEvidence(value.isNotEmpty() && value.none { it <= ' ' || it == '\\' })
    val uri = URI(value)
    requireEvidence(uri.scheme?.lowercase() in setOf("http", "https") && uri.rawUserInfo == null && uri.rawFragment == null)
    val host = uri.host?.lowercase()?.removePrefix("[")?.removeSuffix("]") ?: throw Rejected("invalid_input")
    requireEvidence(uri.port == -1 || uri.port in 1..65535)
    requireEvidence(host.isNotEmpty() && !host.endsWith('.') && host != "localhost" && !listOf(".localhost", ".local", ".internal", ".test", ".invalid").any { host.endsWith(it) })
    if (host.contains(':') || host.matches(Regex("[0-9.]+"))) {
        if (!host.contains(':')) requireEvidence(host.split('.').size == 4 && host.split('.').all { it.matches(Regex("0|[1-9][0-9]{0,2}")) && it.toInt() <= 255 })
        val address = InetAddress.getByName(host)
        val bytes = address.address.map { it.toInt() and 255 }
        requireEvidence(!address.isAnyLocalAddress && !address.isLoopbackAddress && !address.isLinkLocalAddress && !address.isSiteLocalAddress && !address.isMulticastAddress)
        if (bytes.size == 4) requireEvidence(bytes[0] != 0 && bytes[0] < 224 &&
            !(bytes[0] == 100 && bytes[1] in 64..127) &&
            !(bytes[0] == 192 && bytes[1] == 0 && bytes[2] in setOf(0, 2)) &&
            !(bytes[0] == 192 && bytes[1] == 88 && bytes[2] == 99) &&
            !(bytes[0] == 198 && (bytes[1] in 18..19 || (bytes[1] == 51 && bytes[2] == 100))) &&
            !(bytes[0] == 203 && bytes[1] == 0 && bytes[2] == 113))
        else requireEvidence(bytes[0] in 0x20..0x3f && !(bytes[0] == 0x20 && bytes[1] == 1 && bytes[2] == 0x0d && bytes[3] == 0xb8))
    } else {
        requireEvidence(host.contains('.') && host.split('.').all { it.matches(Regex("[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?")) })
        requireEvidence(!host.split('.').all { it.matches(Regex("(?:0x[0-9a-f]+|[0-9]+)")) })
    }
}

private class GuardedTool<I : Any>(
    name: String,
    description: String,
    type: Class<I>,
    private val decode: (JsonNode) -> I,
    perform: (I) -> GuildEvidence,
) : TypedTool<I, GuildEvidence>(name, description, type, GuildEvidence::class.java, function = Function(perform)) {
    override fun parseInput(input: String): I = decode(objectJson(input, INPUT_LIMIT))

    override fun call(input: String): Tool.Result {
        val result = super.call(input)
        // Parser errors must not echo supplied input or exception details into model context.
        return if (result is Tool.Result.Error) convertResult(GuildEvidence("invalid_input", "invalid_input")) else result
    }
}

/** Opt-in native tools. HTTP is the only substituted seam in the tests. */
class AgentGuildTools internal constructor(private val http: GuildHttp, private val clock: Clock) {
    constructor() : this(JdkGuildHttp(), Clock.systemUTC())

    val observe: TypedTool<EndpointInput, GuildEvidence> = GuardedTool(
        "agent_guild_observe_endpoint",
        "Observe one explicitly selected public endpoint through Agent Guild. Returns measured checks and unknowns, not permission to delegate or attach tools. Guild contacts that target. No payment or task execution.",
        EndpointInput::class.java,
        { node -> exactFields(node, setOf("url")); EndpointInput(node.text("url")) },
        ::observeEndpoint,
    )

    val verify: TypedTool<PassportInput, GuildEvidence> = GuardedTool(
        "agent_guild_verify_passport",
        "Verify a supplied public Guild passport against separately expected issuer, subject and maximum age. Sends the complete public credential to Guild, which logs verification even on failure. Origin and integrity do not prove truth, safety or endpoint ownership.",
        PassportInput::class.java,
        { node ->
            exactFields(node, setOf("credentialJson", "expectedIssuerDid", "expectedSubjectDid", "maxAgeSeconds"))
            val age = node.get("maxAgeSeconds")
            requireEvidence(age != null && age.isIntegralNumber && age.canConvertToLong())
            PassportInput(node.text("credentialJson"), node.text("expectedIssuerDid"), node.text("expectedSubjectDid"), age.asLong())
        },
        ::verifyPassport,
    )

    fun toolGroup(): ToolGroup = ToolGroup.ofTools(
        ToolGroupMetadata(
            description = "Optional public endpoint observations and supplied public passport verification",
            role = "agent-guild", name = "agent-guild", provider = "AgentTanuki",
            permissions = setOf(ToolGroupPermission.INTERNET_ACCESS),
        ),
        listOf(observe, verify),
    )

    private fun response(request: GuildRequest): JsonNode {
        val response = http.exchange(request)
        requireEvidence(response.status == 200, "http_unavailable")
        requireEvidence(response.contentType.substringBefore(';').trim().lowercase() == "application/json", "invalid_response")
        requireEvidence(response.contentEncoding.isBlank() || response.contentEncoding.equals("identity", true), "invalid_response")
        requireEvidence(response.body.size <= BODY_LIMIT, "response_too_large")
        val text = UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(response.body)).toString()
        return try { objectJson(text, BODY_LIMIT) } catch (_: Exception) { throw Rejected("invalid_response") }
    }

    private fun observeEndpoint(input: EndpointInput): GuildEvidence {
        try { publicEndpoint(input.url) } catch (_: Exception) { return GuildEvidence("invalid_input", "invalid_input") }
        return remote {
            val node = response(GuildRequest("GET", URI("$ORIGIN/preflight?url=${URLEncoder.encode(input.url, UTF_8)}")))
            requireEvidence(node.text("target") == input.url, "target_mismatch")
            val rows = node.get("checks")
            requireEvidence(rows != null && rows.isArray && rows.size() == checkNames.size, "invalid_response")
            val checks = linkedMapOf<String, String>()
            rows.forEach { row ->
                requireEvidence(row.isObject, "invalid_response")
                val name = row.text("check"); val status = row.text("status")
                requireEvidence(name in checkNames && name !in checks && status in setOf("proven", "failed", "unknown"), "invalid_response")
                checks[name] = status
            }
            val failed = checkNames.filter { checks[it] == "failed" }
            val unknowns = checkNames.filter { checks[it] == "unknown" }
            val scored = checkNames.filter { checks[it] != "unknown" }
            for ((key, expected) in listOf("failed" to failed, "unknowns" to unknowns, "scored" to scored)) {
                val values = node.get(key)
                requireEvidence(values != null && values.isArray && values.all { it.isString } && values.toList().map { it.asString() }.toSet() == expected.toSet() && values.size() == expected.size, "invalid_response")
            }
            val verdict = if (failed.any { it in checkNames.take(2) }) "do_not_delegate" else if (failed.isNotEmpty()) "delegate_with_caution" else "no_failed_checks"
            requireEvidence(node.text("verdict") == verdict, "invalid_response")
            GuildEvidence("completed", target = input.url, observedAt = clock.instant().toString(), verdict = verdict, checks = checkNames.associateWith { checks.getValue(it) }, failed = failed, unknowns = unknowns)
        }
    }

    private fun passport(input: PassportInput, now: Instant): JsonNode {
        requireEvidence(input.maxAgeSeconds in 1..86400)
        for (did in listOf(input.expectedIssuerDid, input.expectedSubjectDid)) requireEvidence(did.matches(Regex("did:key:z[1-9A-HJ-NP-Za-km-z]{20,120}")))
        val vc = objectJson(input.credentialJson, BODY_LIMIT)
        requireEvidence(vc.text("issuer") == input.expectedIssuerDid, "issuer_mismatch")
        val subject = vc.get("credentialSubject")
        requireEvidence(subject != null && subject.isObject && subject.text("id") == input.expectedSubjectDid, "subject_mismatch")
        val types = vc.get("type"); val context = vc.get("@context")
        requireEvidence(types != null && types.isArray && types.all { it.isString } && types.toList().map { it.asString() }.containsAll(listOf("VerifiableCredential", "AgentGuildPassport")))
        requireEvidence(context != null && context.isArray && context.any { it.isString && it.asString() == "https://www.w3.org/ns/credentials/v2" })
        requireEvidence(vc.text("id").isNotEmpty())
        val proof = vc.get("proof")
        requireEvidence(proof != null && proof.isObject && proof.text("type") == "DataIntegrityProof" && proof.text("cryptosuite") == "eddsa-jcs-2022")
        requireEvidence(proof.text("proofPurpose") == "assertionMethod" && proof.text("verificationMethod") == "${input.expectedIssuerDid}#${input.expectedIssuerDid.removePrefix("did:key:")}")
        requireEvidence(proof.text("proofValue").matches(Regex("z[1-9A-HJ-NP-Za-km-z]{20,200}")))
        val start = Instant.parse(vc.text("validFrom")); val created = Instant.parse(proof.text("created"))
        requireEvidence(start <= now && created <= now && created == start, "not_current")
        requireEvidence(start >= now.minusSeconds(input.maxAgeSeconds), "stale_passport")
        if (vc.has("validUntil")) requireEvidence(Instant.parse(vc.text("validUntil")) > now && Instant.parse(vc.text("validUntil")) > start, "expired_passport")
        return vc
    }

    private fun verifyPassport(input: PassportInput): GuildEvidence {
        val vc = try { passport(input, clock.instant()) } catch (e: Rejected) { return GuildEvidence("invalid_input", e.code) } catch (_: Exception) { return GuildEvidence("invalid_input", "invalid_input") }
        return remote {
            val node = response(GuildRequest("POST", URI("$ORIGIN/credentials/verify"), utf8(input.credentialJson, BODY_LIMIT)))
            requireEvidence(node.get("valid")?.isBoolean == true && node.get("guild_issued")?.isBoolean == true, "invalid_response")
            requireEvidence(node.text("issuer") == input.expectedIssuerDid && node.text("subject_did") == input.expectedSubjectDid, "identity_mismatch")
            // Recheck time after the request: a credential may expire while waiting for HTTP.
            passport(input, clock.instant())
            GuildEvidence("completed", observedAt = clock.instant().toString(), verified = node.get("valid").asBoolean() && node.get("guild_issued").asBoolean(), issuerDid = input.expectedIssuerDid, subjectDid = input.expectedSubjectDid, validFrom = vc.text("validFrom"), validUntil = if (vc.has("validUntil")) vc.text("validUntil") else null)
        }
    }

    private fun remote(operation: () -> GuildEvidence): GuildEvidence = try {
        operation()
    } catch (e: Rejected) {
        GuildEvidence("unavailable", if (e.code == "invalid_input") "invalid_response" else e.code)
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        GuildEvidence("unavailable", "request_interrupted")
    } catch (_: Exception) {
        GuildEvidence("unavailable", "request_failed")
    }
}

@Configuration(proxyBeanMethods = false)
@Profile("agent-guild")
class AgentGuildConfiguration {
    @Bean
    fun agentGuildToolGroup(): ToolGroup = AgentGuildTools().toolGroup()
}
