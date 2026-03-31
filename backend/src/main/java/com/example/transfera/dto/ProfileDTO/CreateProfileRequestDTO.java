package com.example.transfera.dto.ProfileDTO;

import com.example.transfera.domain.user.UserCredentials;
import lombok.Data;

@Data
public class CreateProfileRequestDTO {

    private String userName;
    private String firstName;
    private String lastName;
    private String phoneNumber;

}
