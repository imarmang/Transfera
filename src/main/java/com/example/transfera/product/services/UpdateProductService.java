package com.example.transfera.product.services;

import com.example.transfera.Command;
import com.example.transfera.product.ProductRepository;
import com.example.transfera.product.model.Product;
import com.example.transfera.product.model.ProductDTO;
import com.example.transfera.product.model.UpdateProductCommand;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdateProductService implements Command<UpdateProductCommand, ProductDTO>  {

    private final ProductRepository productRepository;

    public UpdateProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    // CacheEvict -> throws the cache away with the matching id
    // Cahce Put -> throws it away then puts the return value of the method
    @CachePut( value="productCache", key="#command.getId()" )
    public ResponseEntity<ProductDTO> execute ( UpdateProductCommand command ){
        Optional<Product> productOptional = productRepository.findById( command.getId() );
        if ( productOptional.isPresent() ) {
            Product product = command.getProduct();
            product.setId(command.getId() );
            productRepository.save( product );
            return ResponseEntity.ok( new ProductDTO( product ) );
        }

        return null;
    }
}
