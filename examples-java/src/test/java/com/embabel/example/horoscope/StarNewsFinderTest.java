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
package com.embabel.example.horoscope;

import com.embabel.agent.domain.library.NewsStory;
import com.embabel.agent.domain.library.RelevantNewsStories;
import com.embabel.agent.test.unit.FakeOperationContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StarNewsFinderTest {

    @Test
    void newsSelectionPreservesSourceDataAndRemovesDuplicates() {
        var feed = mock(NewsFeedService.class);
        var finder = new StarNewsFinder(mock(HoroscopeService.class), feed, 5, "test-model");
        var context = new FakeOperationContext();
        var stories = new RelevantNewsStories(Arrays.asList(new NewsStory(
                "https://example.com/news/training", "Training opportunities", "New training courses announced.")));
        when(feed.latestStories(any())).thenReturn(stories.getItems());
        context.expectResponse(new StarNewsFinder.NewsSelection(Arrays.asList(0, 0)));

        var result = finder.findNewsStories(new StarPerson("Arnab", "Aries"),
                new Horoscope("Focus on your career"), context.ai());

        assertEquals(1, result.getItems().size());
        assertSame(stories.getItems().getFirst(), result.getItems().getFirst());
        var invocations = context.getLlmInvocations();
        assertEquals(1, invocations.size());
        assertTrue(invocations.getFirst().getMessages().getFirst().getContent().contains("Training opportunities"));
        assertTrue(invocations.getLast().getInteraction().getToolGroups().isEmpty(),
                "Selection must not repeat web tool calls");
    }

    @Test
    void rejectsUnknownStoryIndex() {
        var feed = mock(NewsFeedService.class);
        when(feed.latestStories(any())).thenReturn(Arrays.asList(new NewsStory("https://example.com/news", "News", "Summary")));
        var finder = new StarNewsFinder(mock(HoroscopeService.class), feed, 5, "test-model");
        var context = new FakeOperationContext();
        context.expectResponse(new StarNewsFinder.NewsSelection(Arrays.asList(1)));
        assertThrows(IllegalStateException.class, () -> finder.findNewsStories(
                new StarPerson("Arnab", "Aries"), new Horoscope("Career"), context.ai()));
    }

    @Nested
    class Writeup {

        @Test
        void writeupPromptMustContainKeyData() {

            HoroscopeService horoscopeService = mock(HoroscopeService.class);
            StarNewsFinder starNewsFinder = new StarNewsFinder(horoscopeService, mock(NewsFeedService.class), 5, "test-model");
            var context = new FakeOperationContext();
            context.expectResponse("Gonna be a good day");

            NewsStory cockatoos = new NewsStory(
                    "https://fake.com.au",
                    "Cockatoo behavior",
                    "Cockatoos are eating cabbages"
            );

            NewsStory emus = new NewsStory(
                    "https://morefake.com.au",
                    "Emu movements",
                    "Emus are massing"
            );

            StarPerson starPerson = new StarPerson("Lynda", "Scorpio");
            RelevantNewsStories relevantNewsStories = new RelevantNewsStories(Arrays.asList(cockatoos, emus));
            Horoscope horoscope = new Horoscope("This is a good day for you");

            var result = starNewsFinder.writeup(starPerson, relevantNewsStories, horoscope, context.ai());
            assertTrue(result.getContent().startsWith("Gonna be a good day"));
            assertTrue(result.getContent().contains("[Cockatoo behavior](https://fake.com.au)"));
            assertTrue(result.getContent().contains("[Emu movements](https://morefake.com.au)"));

            var prompt = context.getLlmInvocations().getFirst().getMessages().getFirst().getContent();
            var toolGroups = context.getLlmInvocations().getFirst().getInteraction().getToolGroups();


            assertTrue(prompt.contains(starPerson.getName()));
            assertTrue(prompt.contains(starPerson.sign()));
            assertTrue(prompt.contains(cockatoos.getSummary()));
            assertTrue(prompt.contains(emus.getSummary()));

            assertTrue(toolGroups.isEmpty(), "The LLM should not have been given any tool groups");
        }
    }
}
