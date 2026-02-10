package com.example.transfera.product.headers;

import com.example.transfera.product.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeaderController {

    @GetMapping( "/header" )
    public String getRegionalResponse( @RequestHeader ( required = false, defaultValue = "US" ) String region ) {
        // NORMALLY ABSTRACT THIS OUT INTO A SERVICE CLASS
        if ( region.equals( "US" ) ) return "BALD EAGLE FREEDOM";
        if ( region.equals( "CAN" ) ) return "MAPLE SYRUP";

        return "COUNTRY NOT SUPPORTED";
    }

    @GetMapping( value = "/header/product", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE } )
    public ResponseEntity<Product> getProduct() {
        Product product = new Product();
        product.setId( 1 );
        product.setName( "super great product" );
        product.setDescription( "The greatest product you'll ever see ever" );

        return ResponseEntity.ok(product);
    }
}
