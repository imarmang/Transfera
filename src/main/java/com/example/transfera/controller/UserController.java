package com.example.transfera.controller;

import com.example.transfera.domain.user.User;
import com.example.transfera.dto.UserDTO.UserDTO;
import com.example.transfera.service.user.CreateUserService;
import com.example.transfera.service.user.DeleteUserService;
import com.example.transfera.service.user.GetUsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/useraccounts")
public class UserController {

    private final CreateUserService createUserService;
    private final GetUsersService getUsersService;
    private final DeleteUserService deleteUserService;

    public UserController(CreateUserService createUserService, GetUsersService getUsersService, DeleteUserService deleteUserService) {
        this.createUserService = createUserService;
        this.getUsersService = getUsersService;
        this.deleteUserService = deleteUserService;
    }

    @PutMapping( "/user/{id}" )
    public ResponseEntity<UserDTO> execute( @RequestBody User user ) {
        return createUserService.execute( user );
    }

    @GetMapping( "/users" )
    public ResponseEntity<List<UserDTO>> execute() {
        return getUsersService.execute( null );
    }

    @DeleteMapping( "/user/{id}")
    public ResponseEntity<Void> execute( @PathVariable UUID id ) {
        return deleteUserService.execute( id );
    }
}
