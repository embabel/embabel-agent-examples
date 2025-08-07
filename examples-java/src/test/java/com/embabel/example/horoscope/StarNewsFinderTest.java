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
package com.embabel.example.horoscope;

import com.embabel.agent.domain.library.NewsStory;
import com.embabel.agent.domain.library.RelevantNewsStories;
import com.embabel.agent.testing.unit.FakeOperationContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class StarNewsFinderTest {

  @Nested
  class Writeup {

    @Test
    void writeupPromptMustContainKeyData() {

      HoroscopeService horoscopeService = mock(HoroscopeService.class);
      StarNewsFinder starNewsFinder = new StarNewsFinder(horoscopeService, 5);
      var context = new FakeOperationContext();
      var runner = context.getPromptRunner();
      context.expectResponse(new com.embabel.example.horoscope.Writeup("Gonna be a good day"));

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

      starNewsFinder.writeup(starPerson, relevantNewsStories, horoscope, context);

      var prompt = context.getLlmInvocations().getFirst().getPrompt();
      var toolGroups = context.getLlmInvocations().getFirst().getInteraction().getToolGroups();


      assertTrue(prompt.contains(starPerson.getName()));
      assertTrue(prompt.contains(starPerson.sign()));
      assertTrue(prompt.contains(cockatoos.getSummary()));
      assertTrue(prompt.contains(emus.getSummary()));

      assertTrue(toolGroups.isEmpty(), "The LLM should not have been given any tool groups");
    }
  }
}
