package com.example.transfera;

import com.example.transfera.exceptions.ProductNotFoundException;
import com.example.transfera.product.ProductRepository;
import com.example.transfera.product.model.Product;
import com.example.transfera.product.model.ProductDTO;
import com.example.transfera.product.services.GetProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class GetProductServiceTest {

    @Mock  // WHAT TO MOCK THE RESPONSE OF -> NEED THIS DEPENDENCY TO RUN THIS TEST
    private ProductRepository productRepository;

    @InjectMocks  // THE CLASS WE ARE ACTUALLY TESTING
    private GetProductService getProductService;

    @BeforeEach   // things we need before the test runs to set up properly
    public void setUp() {
        // initializes the repository and test service
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void given_product_exists_when_get_product_service_return_product_dto() {
        Product product = new Product();
        product.setId( 1 );
        product.setName( "Product Name" );
        product.setDescription( "Product Description which is at least 20 chars" );

        //ACTUALLY STILL SETTING UP
        when( productRepository.findById( 1 ) ).thenReturn( Optional.of( product ) );

        // when
        ResponseEntity<ProductDTO> response = getProductService.execute( 1 );


        // then
        assertEquals( ResponseEntity.ok( new ProductDTO( product ) ), response );

        verify(productRepository, times( 1 ) ).findById( 1 );
    }

    @Test
    public void given_product_does_not_exist_when_get_product_service_throw_product_not_found_exception() {
        // Given
        when( productRepository.findById( 1 ) ).thenReturn(Optional.empty() );

        // When & THen
        assertThrows( ProductNotFoundException.class, ()-> getProductService.execute( 1 ) );

        verify( productRepository, times( 1 ) ).findById( 1 );
    }
}
