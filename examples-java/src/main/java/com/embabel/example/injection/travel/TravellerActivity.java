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

import org.springframework.ai.tool.annotation.Tool;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Record of customer activity over a period of time.
 * We add methods exposed to be exposed the LLM as tools.
 *
 * @param from
 * @param to
 * @param trips
 */
public record TravellerActivity(
        String name,
        Instant from,
        Instant to,
        List<Trip> trips
) {

    @Tool
    public float totalSpent() {
        return trips.stream().map(Trip::amount).reduce(0f, Float::sum);
    }

    @Tool
    public float averageSpend() {
        return trips.isEmpty() ?
                0f :
                totalSpent() / trips.size();
    }

    @Tool(description = "Get the number of trips taken in the period")
    public int tripCount() {
        return trips.size();
    }

    @Tool(description = "Get the number of days in the period")
    public long periodDays() {
        return Duration.between(from, to).toDays();
    }

    @Tool(description = "Get the distinct destinations visited in the period")
    public List<String> destinations() {
        return trips.stream().map(Trip::to).distinct().toList();
    }

    /**
     * At this rate, how many trips would be taken in a year?
     */
    @Tool(description = "Trips per year")
    public float tripsPerYear() {
        long days = periodDays();
        return days == 0 ?
                trips.size()
                : (trips.size() * 365f) / days;
    }
}
