package com.example.transfera.controller;

import com.example.transfera.dto.ProfileDTO.CreateProfileRequestDTO;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import com.example.transfera.dto.ProfileDTO.SearchProfileDTO;
import com.example.transfera.service.profile.CreateProfileService;

import com.example.transfera.service.profile.GetProfileService;
import com.example.transfera.service.profile.SearchProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/api/v1/profiles" )
public class ProfileController {

    private final CreateProfileService createProfileService;
    private final GetProfileService getProfileService;
    private final SearchProfileService searchProfileService;

    public ProfileController(CreateProfileService createProfileService, GetProfileService getProfileService, SearchProfileService searchProfileService) {
        this.createProfileService = createProfileService;
        this.getProfileService = getProfileService;
        this.searchProfileService = searchProfileService;
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

    // SEARCH USERS BY USERNAME
    @GetMapping( "/search" )
    public ResponseEntity<List<SearchProfileDTO>> searchProfiles(@RequestParam String username ) {
        return searchProfileService.execute( username );
    }
}
