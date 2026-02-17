package com.example.transfera.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table( name="app_user" )
public class User {
    @Id
    @Column( name="id", updatable = false, nullable = false )
    private UUID id;

    @Column ( name="username", length=50 )
    private String username;

    @Column ( name="password", length=300 )
    private String password;

    @Column ( name="email", length=100 )
    private String email;

    @Column ( name="first_name", length=50 )
    private String firstName;

    @Column ( name="last_name", length=50 )
    private String lastName;

    @Column ( name="phone_number", length=50 )
    private String phoneNumber;

}
