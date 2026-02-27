package com.example.transfera.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table( name="app_user" )
public class UserCredentials {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name="id", updatable = false, nullable = false )
    private UUID id;

    @Column ( name="email", length=100, unique = true, nullable = false )
    private String email;

    @Column ( name="password", length=300 )
    private String password;

    public UserCredentials(String email, String password ) {
        this.email = email;
        this.password = password;
    }
}
