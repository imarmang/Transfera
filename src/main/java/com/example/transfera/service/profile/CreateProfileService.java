package com.example.transfera.service.profile;


import com.example.transfera.Command;
import com.example.transfera.domain.profile.Profile;
import com.example.transfera.domain.profile.ProfileRepository;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.ProfileDTO.CreateProfileRequestDTO;
import com.example.transfera.dto.ProfileDTO.ProfileDTO;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
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
        System.out.println(">>> [CreateProfileService] Executing for email: " + email);

        // 2. Look up for User Credentials
        UserCredentials userCredentials = userCredentialsRepository.findByEmail( email ).orElseThrow( () -> {
            System.out.println(">>> [CreateProfileService] User not found for email: " + email);
            return new UserNotFound();
        });
        System.out.println(">>> [CreateProfileService] Found user credentials for: " + email);

        // 3. Check if profile already exists for this user
        if ( profileRepository.existsByUserCredentials( userCredentials ) ) {
            System.out.println(">>> [CreateProfileService] CONFLICT - profile already exists for: " + email);
            return ResponseEntity.status( HttpStatus.CONFLICT ).build();
        }

        // 4. Check if the username is already taken
        if ( profileRepository.existsByUserName( request.getUserName() ) ) {
            System.out.println(">>> [CreateProfileService] CONFLICT - username already taken: " + request.getUserName());
            return ResponseEntity.status( HttpStatus.CONFLICT ).build();
        }

        System.out.println(">>> [CreateProfileService] Saving profile for: " + email + " username: " + request.getUserName());

        // 5. Create and save the profile
        Profile saved = profileRepository.save(
                new Profile(
                        request.getUserName(),
                        request.getFirstName(),
                        request.getLastName(),
                        request.getPhoneNumber(),
                        userCredentials
                )
        );

        System.out.println(">>> [CreateProfileService] Profile saved with id: " + saved.getId());
        return ResponseEntity.status( HttpStatus.CREATED ).body( new ProfileDTO( saved ) );
    }
//    @Override
//    public ResponseEntity<ProfileDTO> execute( CreateProfileRequestDTO request ) {
//
//        // 1. Get email from SecurityContext
//        String email = ( String ) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//        // 2. Look up for User Credentials
//        UserCredentials userCredentials = userCredentialsRepository.findByEmail( email ).orElseThrow( UserNotFound::new );
//
//        // 3. Check if profile already exists for this user
//        if ( profileRepository.existsByUserCredentials( userCredentials ) ) {
//            return ResponseEntity.status( HttpStatus.CONFLICT ).build();
//        }
//
//        // 4. Check if the username is already taken
//        if ( profileRepository.existsByUserName( request.getUserName() ) ) {
//            return ResponseEntity.status( HttpStatus.CONFLICT ).build();
//        }
//
//        // 5. Create and save the profile
//        Profile saved = profileRepository.save(
//            new Profile(
//                    request.getUserName(),
//                    request.getFirstName(),
//                    request.getLastName(),
//                    request.getPhoneNumber(),
//                    userCredentials
//            )
//        );
//
//        return ResponseEntity.status( HttpStatus.CREATED ).body( new ProfileDTO( saved ) );
//    }
}
