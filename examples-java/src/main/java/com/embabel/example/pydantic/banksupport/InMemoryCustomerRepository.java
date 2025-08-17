package com.embabel.example.pydantic.banksupport;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
class InMemoryCustomerRepository implements CustomerRepository {

    private final List<Customer> customers = new ArrayList<>();

    {
        // Pre-populate with customer
        customers.add(new Customer(123L, "John", 100.0f, 27.0f));
    }

    @Override
    public Customer findById(Long id) {
        return customers.stream()
                .filter(customer -> customer.id().equals(id))
                .findFirst()
                .orElse(null);
    }


}
