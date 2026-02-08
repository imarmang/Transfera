package com.example.transfera.product.services;

import com.example.transfera.Command;
import com.example.transfera.product.ProductRepository;
import com.example.transfera.product.model.Product;
import com.example.transfera.product.model.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateProductService implements Command<Product, ProductDTO> {

    private final ProductRepository productRepository;
    public CreateProductService( ProductRepository productRepository ) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<ProductDTO> execute( Product product ) {

        Product savedProduct = productRepository.save( product );
        return ResponseEntity.status( HttpStatus.CREATED ).body( new ProductDTO( savedProduct ) );
    }


}
