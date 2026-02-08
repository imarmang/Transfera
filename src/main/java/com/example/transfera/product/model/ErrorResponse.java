package com.example.transfera.product.model;

import lombok.Getter;

@Getter
public class ErrorResponse {

    // CAN MAKE YOUR ERROR MESSAGE AS BIG AS YOU WANT
    private String message;

    public ErrorResponse( String message ) {
        this.message = message;
    }
}
