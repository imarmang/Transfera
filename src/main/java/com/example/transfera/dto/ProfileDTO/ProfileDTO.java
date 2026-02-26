package com.example.transfera.dto.ProfileDTO;

import com.example.transfera.domain.profile.Profile;
import lombok.Data;

import java.util.UUID;

@Data
public class ProfileDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public ProfileDTO( Profile profile ) {
        this.id          = profile.getId();
        this.username    = profile.getUsername();
        this.firstName   = profile.getFirstName();
        this.lastName    = profile.getLastName();
        this.phoneNumber = profile.getPhoneNumber();
    }
}
