package com.example.transfera.Exceptions;

public enum ErrorMessages {

    // CAN ADD TO THIS LIST OVER TIME
    // messages all in one place
    PRODUCT_NOT_FOUND( "Product Not Found" ),
    NAME_REQUIRED("Name is required"),
    DESCRIPTION_REQUIRED("Description must be 20 characters"),
    PRICE_CANNOT_BE_NEGATIVE("Price cannot be negative");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
