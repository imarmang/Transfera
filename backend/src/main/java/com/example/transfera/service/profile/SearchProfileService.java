package com.example.transfera.service.profile;

import com.example.transfera.Query;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.dto.ProfileDTO.SearchProfileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProfileService implements Query<String, List<SearchProfileDTO>> {
    private final ProfileRepository profileRepository;

    public SearchProfileService( ProfileRepository profileRepository ) {
        this.profileRepository = profileRepository;
    }


    // Return max 10 matching users from the db
    @Override
    public ResponseEntity<List<SearchProfileDTO>> execute( String username ) {

        if ( username == null || username.isBlank() ) {
            return ResponseEntity.ok( List.of() );
        }

        List<Profile> profiles = profileRepository.findTop10ByUserNameStartingWithIgnoreCase( username );

        List<SearchProfileDTO> result = profiles.stream()
                .map( SearchProfileDTO::new )
                .toList();


        return ResponseEntity.ok( result );
    }
}
