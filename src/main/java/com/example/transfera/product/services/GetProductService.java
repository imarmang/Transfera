package com.example.transfera.product.services;

import com.example.transfera.exceptions.ProductNotFoundException;
import com.example.transfera.Query;
import com.example.transfera.product.ProductRepository;
import com.example.transfera.product.model.Product;
import com.example.transfera.product.model.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetProductService implements Query<Integer, ProductDTO> {

    private final ProductRepository productRepository;

    public GetProductService( ProductRepository productRepository ) {
        this.productRepository = productRepository;
    }

    @Override
    public ResponseEntity<ProductDTO> execute( Integer input ) {

        // USING THE OPTIONAL CLASS THAT HANDLES THE NULLEXCEPTION
        Optional<Product> productOptional = productRepository.findById( input );
        if ( productOptional.isPresent() ) {
            return ResponseEntity.ok( new ProductDTO( productOptional.get() ) );
        }

        throw new ProductNotFoundException();
    }
}
