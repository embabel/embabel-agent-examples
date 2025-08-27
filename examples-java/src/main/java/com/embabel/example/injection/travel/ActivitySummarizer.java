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
package com.embabel.example.injection.travel;

import com.embabel.agent.api.common.Ai;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * Implemented by services that can report travel activity.
 * A real implementation would be backed by a database or other software system
 */
interface TravelActivityReportingService {

    /**
     * Given a customer id, return their travel activity
     */
    @Nullable
    TravellerActivity report(Long customerId);
}

/**
 * Spring service that uses Embabel AI to summarize customer activity.
 */
@Service
public record ActivitySummarizer(
        TravelActivityReportingService travelActivityReportingService,
        Ai ai,
        Config config) {

    @ConfigurationProperties(prefix = "example.activity-summarizer")
    public record Config(
            @DefaultValue("80") int maxWords,
            @DefaultValue("2000.0") float highSpenderThreshold,
            @DefaultValue("10") float highTripsPerYearThreshold
    ) {
    }

    @Nullable
    public TravelerActivityReport summarizeActivity(Long customerId) {
        // This may well be transactional, so we don't keep the
        // transaction open while we call the LLM.
        // The tools we pass to the LLM will be fast as they
        // act on data in memory
        var activity = travelActivityReportingService.report(customerId);
        if (activity == null) {
            return null;
        }
        // We can rely on the record's generated toString in this
        // case to expose the right data, as it will include all fields
        var report = ai
                .withDefaultLlm()
                .withToolObject(activity)
                .generateText("""
                        Your job is to help staff of Antechinus Travel understand their customers.
                        You will be given a report of customer activity over a period of time.
                        You should summarize the activity in a way that draws our staff member's
                        attention to important details and also includes the customer's name.
                        
                        Summarize the following customer activity in a friendly way in no more than %s words.
                        We consider customers that spend more than %f to be high spenders.
                        Customers that take more than %f trips per year are frequent travelers.
                        If the customer is a high spender, include a note about this in the summary.
                        
                        # CUSTOMER ACTIVITY
                        %s
                        """.formatted(config.maxWords, config.highSpenderThreshold, config.highTripsPerYearThreshold(),
                        activity));
        return new TravelerActivityReport(report, activity);
    }
}
