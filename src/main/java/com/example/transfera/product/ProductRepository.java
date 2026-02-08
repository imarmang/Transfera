package com.example.transfera.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.transfera.product.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository< Product, Integer > {

    // SPRING DATA JPA
    List<Product> findByNameContaining( String name );

    // JPQL CUSTOM QUERY
    @Query ("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Product> findByNameOrDescriptionContaining(@Param( "keyword" ) String name );

}
