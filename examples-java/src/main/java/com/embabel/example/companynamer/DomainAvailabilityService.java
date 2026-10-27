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

import org.springframework.stereotype.Service;

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class DomainAvailabilityService {

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "(?=.{1,253}\\.?$)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?");

    private final DnsResolver dnsResolver;
    private final Map<String, DomainAvailability> cache = new ConcurrentHashMap<>();

    public DomainAvailabilityService() {
        this(InetAddress::getAllByName);
    }

    DomainAvailabilityService(DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    public DomainAvailability check(String domain) {
        String normalizedDomain;
        try {
            normalizedDomain = normalize(domain);
        } catch (IllegalArgumentException ex) {
            return new DomainAvailability(
                    domain,
                    DomainAvailability.Status.UNKNOWN,
                    "The proposed domain name is invalid: " + ex.getMessage());
        }
        return cache.computeIfAbsent(normalizedDomain, this::resolve);
    }

    private DomainAvailability resolve(String domain) {
        try {
            dnsResolver.resolve(domain);
            return new DomainAvailability(
                    domain,
                    DomainAvailability.Status.IN_USE,
                    "The domain has DNS records and is already in use.");
        } catch (UnknownHostException ex) {
            return new DomainAvailability(
                    domain,
                    DomainAvailability.Status.POTENTIALLY_AVAILABLE,
                    "No DNS records were found. Confirm availability with a registrar before purchasing.");
        } catch (Exception ex) {
            return new DomainAvailability(
                    domain,
                    DomainAvailability.Status.UNKNOWN,
                    "The DNS lookup could not be completed. Try again or check with a registrar.");
        }
    }

    private String normalize(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("domain must not be blank");
        }
        var normalized = IDN.toASCII(domain.strip())
                .toLowerCase(Locale.ROOT)
                .replaceFirst("\\.$", "");
        if (!DOMAIN_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("expected a fully qualified domain name");
        }
        return normalized;
    }

    @FunctionalInterface
    interface DnsResolver {
        InetAddress[] resolve(String domain) throws Exception;
    }
}
