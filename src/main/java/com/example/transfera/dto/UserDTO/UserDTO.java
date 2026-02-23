package com.example.transfera.dto.UserDTO;

import com.example.transfera.domain.user.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;


    public UserDTO( User user ) {
        this.id          = user.getId();
        this.username    = user.getUsername();
        this.password    = user.getPassword();
        this.email       = user.getEmail();
        this.firstName   = user.getFirstName();
        this.lastName    = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
    }
}
