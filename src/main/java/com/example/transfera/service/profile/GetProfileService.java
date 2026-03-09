package com.example.transfera.service.profile;


import com.example.transfera.Query;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetProfileService implements Query<Void, ProfileDTO> {
    private final ProfileRepository profileRepository;

     public GetProfileService( ProfileRepository profileRepository ) {
        this.profileRepository = profileRepository;
    }


    @Override
    public ResponseEntity<ProfileDTO> execute( Void input ) {

         // 1. Get the email
        String email = ( String ) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // 2. Look up profile by email
        Optional<Profile> profile = profileRepository.findByUserCredentialsEmail( email );
        return profile
                .map( value -> ResponseEntity.ok( new ProfileDTO( value ) ) )
                .orElse( ResponseEntity.notFound().build() );

    }
}
