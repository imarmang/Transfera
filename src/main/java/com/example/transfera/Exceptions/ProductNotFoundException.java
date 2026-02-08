package com.example.transfera.Exceptions;


public class ProductNotFoundException extends RuntimeException
{
    public ProductNotFoundException()
    {
        super( ErrorMessages.PRODUCT_NOT_FOUND.getMessage() );
    }
}
