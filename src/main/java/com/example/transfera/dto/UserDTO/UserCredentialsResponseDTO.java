package com.example.transfera.dto.UserDTO;

import com.example.transfera.domain.user.UserCredentials;
import lombok.Data;

import java.util.UUID;

@Data
public class UserCredentialsResponseDTO {
    private UUID id;
    private String email;

    public UserCredentialsResponseDTO(UserCredentials userCredentials) {
        this.id          = userCredentials.getId();
        this.email       = userCredentials.getEmail();
    }
}
