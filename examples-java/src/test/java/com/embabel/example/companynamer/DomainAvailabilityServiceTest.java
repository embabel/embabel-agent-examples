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
package com.embabel.example.companynamer;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainAvailabilityServiceTest {

    @Test
    void reportsDomainWithDnsRecordsAsInUse() {
        var service = new DomainAvailabilityService(domain -> new InetAddress[]{
                InetAddress.getLoopbackAddress()
        });

        var result = service.check("Example.COM.");

        assertEquals("example.com", result.domain());
        assertEquals(DomainAvailability.Status.IN_USE, result.status());
    }

    @Test
    void reportsMissingDnsRecordsAsPotentiallyAvailable() {
        var service = new DomainAvailabilityService(domain -> {
            throw new UnknownHostException(domain);
        });

        var result = service.check("possible-example.com");

        assertEquals(DomainAvailability.Status.POTENTIALLY_AVAILABLE, result.status());
    }

    @Test
    void cachesDnsResults() {
        var resolutions = new AtomicInteger();
        var service = new DomainAvailabilityService(domain -> {
            resolutions.incrementAndGet();
            return new InetAddress[]{InetAddress.getLoopbackAddress()};
        });

        service.check("example.com");
        service.check("EXAMPLE.COM");

        assertEquals(1, resolutions.get());
    }

    @Test
    void rejectsInvalidDomainsWithoutDnsLookup() {
        var resolutions = new AtomicInteger();
        var service = new DomainAvailabilityService(domain -> {
            resolutions.incrementAndGet();
            return new InetAddress[]{InetAddress.getLoopbackAddress()};
        });

        var result = service.check("not a domain");

        assertEquals(DomainAvailability.Status.UNKNOWN, result.status());
        assertEquals(0, resolutions.get());
    }
}
