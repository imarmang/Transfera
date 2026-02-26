package com.example.transfera.controller;

import com.example.transfera.domain.user.User;
import com.example.transfera.dto.UserDTO.UserDTO;
import com.example.transfera.exceptions.FeatureNotImplemented;
import com.example.transfera.service.user.CreateUserCredentialsService;
import com.example.transfera.service.user.DeleteUserCredentialsService;
import com.example.transfera.service.user.GetUsersCredentialsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class UserCredentialsController {

    private final CreateUserCredentialsService createUserCredentialsService;
    private final GetUsersCredentialsService getUsersCredentialsService;
    private final DeleteUserCredentialsService deleteUserCredentialsService;

    public UserCredentialsController(
            CreateUserCredentialsService createUserCredentialsService,
            GetUsersCredentialsService getUsersCredentialsService,
            DeleteUserCredentialsService deleteUserCredentialsService ) {
        this.createUserCredentialsService = createUserCredentialsService;
        this.getUsersCredentialsService = getUsersCredentialsService;
        this.deleteUserCredentialsService = deleteUserCredentialsService;
    }


    // GET    /api/v1/users         — list all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        return getUsersCredentialsService.execute( null );
    }

    // POST   /api/v1/users
    // TODO: implement a RequestDTO which would include the password
    @PostMapping
    public ResponseEntity<UserDTO> createUser( @RequestBody User user ) {
        return createUserCredentialsService.execute( user );
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping( "/user/{id}")
    public ResponseEntity<Void> deleteUser( @PathVariable UUID id ) {
        return deleteUserCredentialsService.execute( id );
    }

    // TODO: this will be implemented after the login and registration are completed
    // PATCH  /api/v1/users/{id}/email
    @PatchMapping( "/{id}/email" )
    public ResponseEntity<Void> updateEmail( @RequestBody UserDTO userDTO ) {
        throw new FeatureNotImplemented();
    }

    // TODO: this will be implemented after the login and registration are completed
    // PATCH  /api/v1/users/{id}/password
    @PatchMapping( "/{id}/password" )
    public ResponseEntity<Void> updatePassword( @RequestBody UserDTO userDTO ) {
        throw new FeatureNotImplemented();
    }
}
