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
package com.embabel.example.pydantic.banksupport;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.ai.tool.annotation.Tool;
import jakarta.persistence.GenerationType;

//@Entity

/**
 * Unused JPA Entity.  Keep till further investigation
 */
public class CustomerEntity  {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private float balance;
    private float pendingAmount;

    public CustomerEntity() {
    } // Required by JPA

    public CustomerEntity(Long id, String name, float balance, float pendingAmount) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.pendingAmount = pendingAmount;
    }
    @Tool(description = "Find the balance of a customer by id")
    public float getBalance(boolean includePending) {
        return includePending ? balance + pendingAmount : balance;
    }

    // Getters and setters
}
