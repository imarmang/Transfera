package com.example.transfera;

import org.springframework.http.ResponseEntity;

// i = input, o = output
public interface Query< I, O > {

    ResponseEntity<O> execute( I input );
}
