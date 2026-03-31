package com.example.transfera.controller;

import com.example.transfera.dto.ProfileDTO.CreateProfileRequestDTO;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import com.example.transfera.service.profile.CreateProfileService;

import com.example.transfera.service.profile.GetProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/v1/profiles" )
public class ProfileController {

    private final CreateProfileService createProfileService;
    private final GetProfileService getProfileService;

    public ProfileController( CreateProfileService createProfileService, GetProfileService getProfileService ) {
        this.createProfileService = createProfileService;
        this.getProfileService = getProfileService;
    }

    // CREATE A USER PROFILE
    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile( @RequestBody CreateProfileRequestDTO request ) {
        return createProfileService.execute( request );
    }

    // GET THE PROFILE
    @GetMapping( "/me"  )
    public ResponseEntity<ProfileDTO> getProfile() {
        return getProfileService.execute( null );
    }
}
