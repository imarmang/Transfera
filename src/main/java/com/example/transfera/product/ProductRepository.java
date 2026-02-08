package com.example.transfera.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.transfera.product.model.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository< Product, Integer > {

}
