package com.example.transfera.controller;

import com.example.transfera.dto.UserDTO.UserCredentialsResponseDTO;
import com.example.transfera.exceptions.customExceptions.FeatureNotImplemented;
import com.example.transfera.service.userCredential.DeleteUserCredentialsService;
import com.example.transfera.service.userCredential.GetUsersCredentialsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserCredentialsController {

    private final GetUsersCredentialsService getUsersCredentialsService;
    private final DeleteUserCredentialsService deleteUserCredentialsService;

    public UserCredentialsController(
            GetUsersCredentialsService getUsersCredentialsService,
            DeleteUserCredentialsService deleteUserCredentialsService ) {
        this.getUsersCredentialsService = getUsersCredentialsService;
        this.deleteUserCredentialsService = deleteUserCredentialsService;
    }

    // GET    /api/v1/users         — list all users
    @GetMapping
    public ResponseEntity<List<UserCredentialsResponseDTO>> getUsers() {
        return getUsersCredentialsService.execute( null );
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping( "/{id}")
    public ResponseEntity<Void> deleteUser( @PathVariable UUID id ) {
        return deleteUserCredentialsService.execute( id );
    }

    // TODO: this will be implemented after the login and registration are completed
    // PATCH  /api/v1/users/{id}/email
    @PatchMapping( "/{id}/email" )
    public ResponseEntity<Void> updateEmail( @RequestBody UUID id ) {
        throw new FeatureNotImplemented();
    }

    // TODO: this will be implemented after the login and registration are completed
    // PATCH  /api/v1/users/{id}/password
    @PatchMapping( "/{id}/password" )
    public ResponseEntity<Void> updatePassword( @RequestBody UUID id ) {
        throw new FeatureNotImplemented();
    }
}
