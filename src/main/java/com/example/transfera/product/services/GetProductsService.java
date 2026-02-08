package com.example.transfera.product.services;

import com.example.transfera.product.ProductRepository;
import com.example.transfera.Query;
import com.example.transfera.product.model.ProductDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.transfera.product.model.Product;

import java.util.List;

@Service
public class GetProductsService implements Query<Void, List<ProductDTO>> {

    private final ProductRepository productRepository;

    public GetProductsService( ProductRepository productRepository ) {
        this.productRepository = productRepository;
    }


    @Override
    public ResponseEntity<List<ProductDTO>> execute ( Void input ){
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOs = products.stream().map( ProductDTO::new ).toList();

        return ResponseEntity.status( HttpStatus.OK ).body( productDTOs );

    }
}
