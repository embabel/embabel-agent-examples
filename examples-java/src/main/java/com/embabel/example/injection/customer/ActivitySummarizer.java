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
package com.embabel.example.injection.customer;

import com.embabel.agent.api.common.Ai;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
// @Transactional
interface ReportService {

    @Nullable
    CustomerActivity report(Long customerId);
}


@Component
public record ActivitySummarizer(
        ReportService reportService,
        Ai ai,
        Config config) {

    @ConfigurationProperties(prefix = "example.activity-summarizer")
    public record Config(
            @DefaultValue("1000.0") float highSpenderThreshold,
            @DefaultValue("10") int highTripsThreshold
    ) {
    }

    @Nullable
    public CustomerActivityReport summarizeActivity(Long customerId) {
        var activity = reportService.report(customerId);
        if (activity == null) {
            return null;
        }
        // With records, we can rely on the toString in this
        // case to expose the write data
        var report = ai
                .withDefaultLlm()
                .generateText("""
                        Your job is to help staff of Antechinus Travel understand their customers.
                        You will be given a report of customer activity over a period of time.
                        You should summarize the activity in a way that draws our staff member's
                        attention to important details and also includes the customer's name.
                        
                        Summarize the following customer activity in a friendly way.
                        We consider customers that spend more than %f to be high spenders.
                        If the customer is a high spender, include a note about this in the summary.
                        
                        Activity:
                        %s
                        """.formatted(config.highSpenderThreshold, activity));
        return new CustomerActivityReport(report, activity);
    }
}
