package com.example.transfera.dto.ProfileDTO;

import com.example.transfera.domain.profile.Profile;
import lombok.Data;

@Data
public class SearchProfileDTO {
    private String userName;
    private String firstName;
    private String lastName;

    public SearchProfileDTO( Profile profile ) {
        this.userName = profile.getUserName();
        this.firstName = profile.getFirstName();
        this.lastName = profile.getLastName();
    }
}
