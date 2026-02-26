package com.example.transfera.domain.profile;

import com.example.transfera.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity( name="profile" )
public class Profile {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    @Column( name = "id", updatable = false, nullable = false )
    private UUID id;

    @Column ( name="username", length=50, unique = true )
    private String username;

    @Column ( name="first_name", length=50 )
    private String firstName;

    @Column ( name="last_name", length=50 )
    private String lastName;

    @Column ( name="phone_number", length=50 )
    private String phoneNumber;

    @OneToOne ( fetch = FetchType.LAZY )
    @JoinColumn( name="user_id", nullable = false, unique = true )
    private User user;


    public Profile( String username, String firstName, String lastName, String phoneNumber, User user ) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.user = user;
    }
}
