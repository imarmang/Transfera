package com.example.transfera.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity  // maps java to mysql
@Data
@Table( name="product" )

public class Product {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )  // auto generate starting at 1
    @Column( name = "id")
    private Integer id;

    @NotNull( message = "Name is required" )
    @Column( name = "name" )
    private String name;

    @Size( min = 20, message = "Description must be 20 characters long" )
    @Column( name = "description" )
    private String description;

    @PositiveOrZero (  message = "Price must be positive" )
    @Column( name = "price" )
    private Double price;
}
