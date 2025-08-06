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
package com.embabel.example.horoscope

import com.embabel.agent.domain.library.NewsStory
import com.embabel.agent.domain.library.RelevantNewsStories
import com.embabel.agent.testing.unit.FakeOperationContext
import com.embabel.agent.testing.unit.UnitTestUtils
import com.embabel.example.horoscope.Horoscope
import com.embabel.example.horoscope.StarNewsFinder
import com.embabel.example.horoscope.StarPerson
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FakeHoroscopeService : HoroscopeService {

    override fun dailyHoroscope(sign: String): String {
        return "you will be chased by wolves"
    }
}


/**
 * Demonstrates unit testing of an agent
 */
class StarNewsFinderTest {

    @Nested
    inner class Writeup {

        @Test
        fun `writeup prompt must contain key data`() {
            val context = FakeOperationContext()
            val runner = context.promptRunner()

            context.expectResponse(Writeup("Things are gonna get frosty"))

            val wordCount = 100
            val starNewsFinder = StarNewsFinder(
                horoscopeService = mockk(),
                storyCount = 5,
                wordCount = wordCount,
            )
            val cockatoos =
                NewsStory(
                    url = "https://fake.com.au", title = "Cockatoo behavior",
                    summary = "Cockatoos are eating cabbages"
                )
            val emus =
                NewsStory(
                    url = "https://morefake.com.au", title = "Emu movements",
                    summary = "Emus are massing"
                )
            val starPerson = StarPerson(name = "Lynda", sign = "Scorpio")
            val relevantNewsStories = RelevantNewsStories(listOf(cockatoos, emus))

            starNewsFinder.starNewsWriteup(
                person = starPerson,
                relevantNewsStories = relevantNewsStories,
                horoscope = Horoscope("This is a good day for you"),
                context = context
            )

            val prompt = context.llmInvocations.first().prompt
            val toolGroups = context.llmInvocations.first().interaction.toolGroups

            assertTrue(prompt.contains(starPerson.name))
            assertTrue(prompt.contains("Scorpio"))
            assertTrue(prompt.contains(cockatoos.summary))
            assertTrue(prompt.contains(emus.summary))
            assertTrue(prompt.contains(wordCount.toString()))
            assertTrue(
                toolGroups.isEmpty(),
                "The LLM should not have been given any tool groups",
            )
        }
    }

}
