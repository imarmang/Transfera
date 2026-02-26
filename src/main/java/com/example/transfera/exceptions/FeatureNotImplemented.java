package com.example.transfera.exceptions;

public class FeatureNotImplemented extends RuntimeException {
    public FeatureNotImplemented() {
        super(ErrorMessages.FEATURE_NOT_IMPLEMENTED.getMessage())
        ;
    }
}
