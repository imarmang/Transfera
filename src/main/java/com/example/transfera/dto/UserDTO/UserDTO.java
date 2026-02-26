package com.example.transfera.dto.UserDTO;

import com.example.transfera.domain.user.User;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String email;

    public UserDTO( User user ) {
        this.id          = user.getId();
        this.email       = user.getEmail();
    }
}
