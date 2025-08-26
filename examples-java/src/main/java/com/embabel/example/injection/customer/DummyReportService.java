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

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class DummyReportService implements ReportService {

    private static final Random random = new Random();

    // 6 travelers: 3 male, 3 female, including 1 Chinese and 1 Indian name
    private static final String[] TRAVELER_NAMES = {
            "James Mitchell",      // Male - British
            "Sarah Thompson",      // Female - Canadian
            "Wei Chen",           // Male - Chinese
            "Emma van der Berg",  // Female - Dutch
            "Rajesh Patel",       // Male - Indian
            "Claire Dubois"       // Female - French
    };

    // Major cities in Australia, Canada, France, Netherlands, and UK
    private static final String[] AUSTRALIAN_CITIES = {
            "Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide", "Darwin", "Canberra"
    };

    private static final String[] CANADIAN_CITIES = {
            "Toronto", "Vancouver", "Montreal", "Calgary", "Ottawa", "Edmonton", "Winnipeg", "Halifax"
    };

    private static final String[] FRENCH_CITIES = {
            "Paris", "Lyon", "Marseille", "Nice", "Toulouse", "Strasbourg", "Bordeaux", "Lille"
    };

    private static final String[] DUTCH_CITIES = {
            "Amsterdam", "Rotterdam", "The Hague", "Utrecht", "Eindhoven", "Groningen", "Tilburg"
    };

    private static final String[] UK_CITIES = {
            "London", "Manchester", "Birmingham", "Edinburgh", "Glasgow", "Liverpool", "Bristol", "Leeds"
    };

    private static final String[][] ALL_CITIES = {
            AUSTRALIAN_CITIES, CANADIAN_CITIES, FRENCH_CITIES, DUTCH_CITIES, UK_CITIES
    };

    private static final String[] COUNTRY_NAMES = {
            "Australia", "Canada", "France", "Netherlands", "UK"
    };

    public static CustomerActivity generateRealisticRecord() {
        String name = TRAVELER_NAMES[random.nextInt(TRAVELER_NAMES.length)];

        // Generate time period (last 6 months for more realistic travel frequency)
        Instant to = Instant.now();
        Instant from = to.minus(180, ChronoUnit.DAYS);

        // Generate 1-4 realistic trips (most people don't travel constantly)
        int numTrips = 1 + random.nextInt(4);
        List<Trip> trips = new ArrayList<>();

        Instant currentTime = from;
        for (int i = 0; i < numTrips; i++) {
            Trip trip = generateRealisticTrip(currentTime, to);
            trips.add(trip);
            // Space trips out realistically (at least 2 weeks between trips)
            currentTime = trip.arrival().plus(14 + random.nextInt(30), ChronoUnit.DAYS);
        }

        return new CustomerActivity(name, from, to, trips);
    }

    private static Trip generateRealisticTrip(Instant earliestStart, Instant latestEnd) {
        // Select random countries for origin and destination
        int fromCountryIndex = random.nextInt(ALL_CITIES.length);
        int toCountryIndex = random.nextInt(ALL_CITIES.length);

        String fromCity = ALL_CITIES[fromCountryIndex][random.nextInt(ALL_CITIES[fromCountryIndex].length)];
        String toCity = ALL_CITIES[toCountryIndex][random.nextInt(ALL_CITIES[toCountryIndex].length)];

        // Avoid same city travel
        while (fromCity.equals(toCity)) {
            toCountryIndex = random.nextInt(ALL_CITIES.length);
            toCity = ALL_CITIES[toCountryIndex][random.nextInt(ALL_CITIES[toCountryIndex].length)];
        }

        // Generate departure time within allowed range, leaving room for trip duration
        long maxSeconds = latestEnd.getEpochSecond() - earliestStart.getEpochSecond();
        long maxDepartureOffset = maxSeconds - (7 * 24 * 3600); // Leave room for max 7-day trip
        if (maxDepartureOffset <= 0) maxDepartureOffset = maxSeconds / 2;

        long departureOffset = Math.abs(random.nextLong() % maxDepartureOffset);
        Instant departure = earliestStart.plusSeconds(departureOffset);

        // Realistic trip duration based on travel type
        int tripDurationHours;
        boolean isDomestic = fromCountryIndex == toCountryIndex;

        if (isDomestic) {
            // Domestic trips: 1-4 days
            tripDurationHours = (1 + random.nextInt(4)) * 24;
        } else {
            // International trips: 3-10 days
            tripDurationHours = (3 + random.nextInt(8)) * 24;
        }

        Instant arrival = departure.plus(tripDurationHours, ChronoUnit.HOURS);

        // Ensure arrival doesn't exceed the latest end time
        if (arrival.isAfter(latestEnd)) {
            arrival = latestEnd.minus(1, ChronoUnit.HOURS);
        }

        // Generate realistic pricing
        float amount = generateRealisticPrice(fromCountryIndex, toCountryIndex, tripDurationHours / 24);

        return new Trip(fromCity, toCity, departure, arrival, amount);
    }

    private static float generateRealisticPrice(int fromCountryIndex, int toCountryIndex, int durationDays) {
        boolean isDomestic = fromCountryIndex == toCountryIndex;

        if (isDomestic) {
            // Domestic travel pricing
            float basePrice = 150f + random.nextFloat() * 400f; // $150-550 base
            return basePrice + (durationDays * (50f + random.nextFloat() * 100f)); // Add daily costs
        } else {
            // International travel pricing
            float baseFlight = 500f + random.nextFloat() * 1500f; // $500-2000 flight
            float dailyCost = 100f + random.nextFloat() * 200f; // $100-300 per day

            // Distance-based adjustments
            if (isLongHaulFlight(fromCountryIndex, toCountryIndex)) {
                baseFlight += 300f + random.nextFloat() * 700f; // Long-haul premium
            }

            return baseFlight + (durationDays * dailyCost);
        }
    }

    private static boolean isLongHaulFlight(int from, int to) {
        // Australia to anywhere else, or trans-Atlantic/trans-Pacific routes
        return (from == 0 || to == 0) || // Australia involved
                (from == 1 && (to == 2 || to == 3 || to == 4)) || // Canada to Europe
                (to == 1 && (from == 2 || from == 3 || from == 4)); // Europe to Canada
    }

    // Generate all 6 travelers
    public static List<CustomerActivity> generateAllTravelers() {
        List<CustomerActivity> allTravelers = new ArrayList<>();

        for (String travelerName : TRAVELER_NAMES) {
            // Generate time period (last 6 months)
            Instant to = Instant.now();
            Instant from = to.minus(180, ChronoUnit.DAYS);

            // Generate 1-4 realistic trips per traveler
            int numTrips = 1 + random.nextInt(4);
            List<Trip> trips = new ArrayList<>();

            Instant currentTime = from;
            for (int i = 0; i < numTrips && currentTime.isBefore(to.minus(7, ChronoUnit.DAYS)); i++) {
                Trip trip = generateRealisticTrip(currentTime, to);
                trips.add(trip);
                // Space trips out realistically
                currentTime = trip.arrival().plus(14 + random.nextInt(30), ChronoUnit.DAYS);
            }

            allTravelers.add(new CustomerActivity(travelerName, from, to, trips));
        }

        return allTravelers;
    }

    // Test method to demonstrate all travelers
    public static void main(String[] args) {
        List<CustomerActivity> allTravelers = generateAllTravelers();

        System.out.println("=== REALISTIC TRAVEL RECORDS ===\n");

        for (int customerIndex = 0; customerIndex < allTravelers.size(); customerIndex++) {
            CustomerActivity customer = allTravelers.get(customerIndex);

            System.out.println("Customer " + (customerIndex + 1) + ": " + customer.name());
            System.out.println("Period: " + customer.from() + " to " + customer.to());
            System.out.println("Trips:");

            float totalSpent = 0;
            for (int i = 0; i < customer.trips().size(); i++) {
                Trip trip = customer.trips().get(i);
                System.out.printf("  %d. %s → %s%n", i + 1, trip.from(), trip.to());
                System.out.printf("     %s to %s%n", trip.departure(), trip.arrival());
                System.out.printf("     Amount: $%.2f%n", trip.amount());
                totalSpent += trip.amount();
            }

            System.out.printf("Total Spent: $%.2f%n", totalSpent);
            System.out.println("─".repeat(50));
        }
    }

    @Override
    public CustomerActivity report(Long customerId) {
        // In a real implementation, you'd use customerId to get specific customer
        // For demo purposes, cycle through the 6 travelers based on ID
        if (customerId == null) {
            return generateRealisticRecord();
        }

        List<CustomerActivity> allTravelers = generateAllTravelers();
        int index = (int) (customerId % 6);
        return allTravelers.get(index);
    }
}