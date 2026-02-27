package com.example.transfera.dto.UserDTO;

import lombok.Data;

/**
 * Incoming request body for POST /api/v1/users
 * Only credentials — profile is created separately.
 */

@Data
public class CreateUserRequestDTO {

    private String email;
    private String password;

}
