package com.example.transfera.controller;

import com.example.transfera.dto.ProfileDTO.CreateProfileRequestDTO;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import com.example.transfera.service.profile.CreateProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/v1/profiles" )
public class ProfileController {

    private final CreateProfileService createProfileService;

    public ProfileController( CreateProfileService createProfileService ) {
        this.createProfileService = createProfileService;
    }

    // CREATE A USER PROFILE
    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(@RequestBody CreateProfileRequestDTO request ) {
        return createProfileService.execute( request );
    }


}
