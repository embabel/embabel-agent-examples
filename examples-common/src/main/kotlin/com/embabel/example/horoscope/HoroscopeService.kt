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
package com.embabel.example.horoscope

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.net.http.HttpClient

fun interface HoroscopeService {

    fun dailyHoroscope(sign: String): String
}

@Service
class HoroscopeAppApiHoroscopeService : HoroscopeService {

    private val restClient = RestClient.builder()
        .baseUrl("https://horoscope-app-api.vercel.app")
        .requestFactory(
            JdkClientHttpRequestFactory(
                HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
            )
        )
        .build()

    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun dailyHoroscope(sign: String): String {
        val body = restClient.get()
            .uri("/api/v1/get-horoscope/daily?sign={sign}&day=today", sign.lowercase())
            .retrieve()
            .body<String>()
            ?: return "Unable to retrieve horoscope for $sign today."

        val response = objectMapper.readValue(body, HoroscopeResponse::class.java)
        return response.data?.horoscope
            ?: "Unable to retrieve horoscope for $sign today."
    }
}

private data class HoroscopeResponse(
    val data: HoroscopeData?,
)

private data class HoroscopeData(
    val date: String,
    val sign: String,
    val period: String,
    val horoscope: String,
)
