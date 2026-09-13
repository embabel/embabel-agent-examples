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

import java.io.ByteArrayOutputStream
import java.net.ProxySelector
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.ByteBuffer
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit

internal data class GuildRequest(val method: String, val uri: URI, val body: ByteArray? = null)
internal data class GuildResponse(val status: Int, val contentType: String, val body: ByteArray, val contentEncoding: String = "")
internal fun interface GuildHttp {
    fun exchange(request: GuildRequest): GuildResponse
}

internal class JdkGuildHttp(private val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(3))
        .followRedirects(HttpClient.Redirect.NEVER)
        .proxy(ProxySelector.of(null))
        .build()) : GuildHttp {

    override fun exchange(request: GuildRequest): GuildResponse {
        require(request.uri.scheme == "https" && request.uri.rawAuthority == "agent-guild-5d5r.onrender.com")
        require((request.method == "GET" && request.uri.path == "/preflight" && request.body == null) ||
            (request.method == "POST" && request.uri.path == "/credentials/verify" && request.uri.rawQuery == null && request.body != null && request.body.size <= 65_536))
        val builder = HttpRequest.newBuilder(request.uri)
            .timeout(Duration.ofSeconds(10))
            .header("Accept", "application/json")
            .header("Accept-Encoding", "identity")
            .header("User-Agent", "AgentGuild-Embabel-Example/1.0")
        if (request.body == null) builder.GET() else builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofByteArray(request.body))
        val pending = client.sendAsync(builder.build()) { BoundedBody() }
        try {
            val response = pending.get(12, TimeUnit.SECONDS)
            return GuildResponse(response.statusCode(), response.headers().firstValue("content-type").orElse(""), response.body(), response.headers().firstValue("content-encoding").orElse(""))
        } finally {
            // Best-effort cancellation; it cannot revoke remote work already accepted by the server.
            pending.cancel(true)
        }
    }
}

/** Bounds response bytes while streaming, before parsing or model-facing projection. */
internal class BoundedBody : HttpResponse.BodySubscriber<ByteArray> {
    private val result = CompletableFuture<ByteArray>()
    private val bytes = ByteArrayOutputStream()
    private var subscription: Flow.Subscription? = null
    override fun getBody(): CompletionStage<ByteArray> = result
    override fun onSubscribe(value: Flow.Subscription) {
        if (subscription != null) value.cancel() else { subscription = value; value.request(1) }
    }
    override fun onNext(items: List<ByteBuffer>) {
        if (result.isDone) return
        for (item in items) {
            if (item.remaining() > 65_536 - bytes.size()) {
                subscription?.cancel()
                result.completeExceptionally(IllegalStateException("response_too_large"))
                return
            }
            val part = ByteArray(item.remaining()); item.get(part); bytes.write(part)
        }
        subscription?.request(1)
    }
    override fun onError(error: Throwable) { result.completeExceptionally(error) }
    override fun onComplete() { result.complete(bytes.toByteArray()) }
}
