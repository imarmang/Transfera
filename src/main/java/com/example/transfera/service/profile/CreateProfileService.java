package com.example.transfera.service.profile;


import com.example.transfera.Command;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.ProfileDTO.CreateProfileRequestDTO;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import com.example.transfera.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CreateProfileService implements Command<CreateProfileRequestDTO, ProfileDTO> {

    private final ProfileRepository profileRepository;
    private final UserCredentialsRepository userCredentialsRepository;

    public CreateProfileService( ProfileRepository profileRepository, UserCredentialsRepository userCredentialsRepository ) {
        this.profileRepository = profileRepository;
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public ResponseEntity<ProfileDTO> execute( CreateProfileRequestDTO request ) {

        // 1. Get email from SecurityContext
        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Look up for User Credentials
        UserCredentials userCredentials = userCredentialsRepository.findByEmail( email ).orElseThrow(() -> new UserNotFound() );

        // 3. Create and save the profile
        Profile saved = profileRepository.save(
            new Profile(
                    request.getUserName(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhoneNumber(),
                    userCredentials
            )
        );

        return ResponseEntity.status( HttpStatus.CREATED ).body( new ProfileDTO( saved ) );
    }
}
