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
package com.embabel.example.injection.travel;

import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.PromptRunner;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ActivitySummarizerTest {

    @Test
    void testSummarizeActivityNullReport() {
        var mockReportingService = Mockito.mock(TravelActivityReportingService.class);
        var mockAi = Mockito.mock(Ai.class);
        var config = new ActivitySummarizer.Config(80, 2000.0f, 10f);

        when(mockReportingService.report(1L)).thenReturn(null);

        var activitySummarizer = new ActivitySummarizer(mockReportingService, mockAi, config);
        var result = activitySummarizer.summarizeActivity(1L);

        assertNull(result);
        Mockito.verify(mockReportingService).report(1L);
        Mockito.verifyNoInteractions(mockAi);
    }

    @Test
    void testSummarizeActivityLlmThrowsException() {
        var mockReportingService = Mockito.mock(TravelActivityReportingService.class);
        var mockAi = Mockito.mock(Ai.class);
        var mockPromptRunner = Mockito.mock(PromptRunner.class);
        var config = new ActivitySummarizer.Config(80, 2000.0f, 10f);

        var travellerActivity = createSampleTravellerActivity();
        when(mockReportingService.report(1L)).thenReturn(travellerActivity);
        when(mockAi.withDefaultLlm()).thenReturn(mockPromptRunner);
        when(mockPromptRunner.withToolObject(travellerActivity)).thenReturn(mockPromptRunner);
        when(mockPromptRunner.generateText(anyString())).thenThrow(new RuntimeException("LLM service unavailable"));

        var activitySummarizer = new ActivitySummarizer(mockReportingService, mockAi, config);

        assertThrows(RuntimeException.class, () -> activitySummarizer.summarizeActivity(1L));

        Mockito.verify(mockReportingService).report(1L);
        Mockito.verify(mockAi).withDefaultLlm();
        Mockito.verify(mockPromptRunner).withToolObject(travellerActivity);
    }

    @Test
    void testSummarizeActivityValidReturn() {
        var mockReportingService = Mockito.mock(TravelActivityReportingService.class);
        var mockAi = Mockito.mock(Ai.class);
        var mockPromptRunner = Mockito.mock(PromptRunner.class);
        var config = new ActivitySummarizer.Config(80, 2000.0f, 10f);

        var travellerActivity = createSampleTravellerActivity();
        var expectedSummary = "John Doe is a frequent traveler who has taken 2 trips to Paris and Tokyo, spending $3000 total. As a high spender, he enjoys premium destinations.";

        when(mockReportingService.report(1L)).thenReturn(travellerActivity);
        when(mockAi.withDefaultLlm()).thenReturn(mockPromptRunner);
        when(mockPromptRunner.withToolObject(travellerActivity)).thenReturn(mockPromptRunner);
        when(mockPromptRunner.generateText(anyString())).thenReturn(expectedSummary);

        var activitySummarizer = new ActivitySummarizer(mockReportingService, mockAi, config);
        var result = activitySummarizer.summarizeActivity(1L);

        assertEquals(expectedSummary, result.summary());
        assertEquals(travellerActivity, result.activity());

        Mockito.verify(mockReportingService).report(1L);
        Mockito.verify(mockAi).withDefaultLlm();
        Mockito.verify(mockPromptRunner).withToolObject(travellerActivity);
        Mockito.verify(mockPromptRunner).generateText(anyString());
    }

    private TravellerActivity createSampleTravellerActivity() {
        var trip1 = new Trip("New York", "Paris", Instant.parse("2024-01-01T10:00:00Z"), Instant.parse("2024-01-01T20:00:00Z"), 1500f);
        var trip2 = new Trip("Paris", "Tokyo", Instant.parse("2024-02-01T08:00:00Z"), Instant.parse("2024-02-01T22:00:00Z"), 1500f);

        return new TravellerActivity(
                "John Doe",
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-12-31T23:59:59Z"),
                List.of(trip1, trip2)
        );
    }
}