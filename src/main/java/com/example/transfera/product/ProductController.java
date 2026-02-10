package com.example.transfera.product;

import com.example.transfera.product.model.Product;
import com.example.transfera.product.model.ProductDTO;
import com.example.transfera.product.model.UpdateProductCommand;
import com.example.transfera.product.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final CreateProductService createProductService;
    private final GetProductsService getProductsService;
    private final DeleteProductService deleteProductService;
    private final UpdateProductService updateProductService;
    private final GetProductService getProductService;
    private final SearchProductService searchProductService;

    public ProductController( CreateProductService createProductService,
                              GetProductsService getProductsService,
                              DeleteProductService deleteProductService,
                              UpdateProductService updateProductService,
                              GetProductService getProductService,
                              SearchProductService searchProductService ) {
        this.createProductService = createProductService;
        this.getProductsService = getProductsService;
        this.deleteProductService = deleteProductService;
        this.updateProductService = updateProductService;
        this.getProductService = getProductService;
        this.searchProductService = searchProductService;
    }

    @PostMapping( "product" )
    public ResponseEntity<ProductDTO> createProduct( @RequestBody Product product ) {
        return createProductService.execute( product );
    }

    @GetMapping ("/products" )
    public ResponseEntity<List<ProductDTO>> getProducts() {
        return getProductsService.execute( null );
    }

    @GetMapping( "/product/{id}" )
    public ResponseEntity<ProductDTO> getProductById( @PathVariable Integer id ) {
        return getProductService.execute( id );
    }

    @GetMapping( "/product/search" )
    public ResponseEntity<List<ProductDTO>> searchProductByName( @RequestParam String name ) {
        return searchProductService.execute( name );
    }


    @PutMapping( "/product/{id}" )
    public ResponseEntity<ProductDTO> updateProduct( @PathVariable Integer id, @RequestBody Product product ) {

        return updateProductService.execute( new UpdateProductCommand( id, product ) );
    }

    @DeleteMapping( "/product/{id}" )  // id must match id in
    public ResponseEntity<Void> deleteProduct( @PathVariable Integer id ) {
        return deleteProductService.execute( id );
    }
}
