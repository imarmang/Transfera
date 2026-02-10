package com.example.transfera.mappings;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/customer" )
public class CustomerController {

    private CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping( "/{id}" )
    public ResponseEntity<Customer> getCustomerById( @PathVariable Integer id ) {
        // normally -> abstract logic into service class, but we are keeping it simple right now
        return ResponseEntity.ok( customerRepository.findById( id ).get() );
    }
}
