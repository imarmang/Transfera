package com.example.transfera.controller;

import com.example.transfera.domain.user.User;
import com.example.transfera.dto.UserDTO.UserDTO;
import com.example.transfera.service.user.CreateUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/useraccounts")
public class UserController {

    public UserController( CreateUserService createUserService ) {
        this.createUserService = createUserService;
    }

    private final CreateUserService createUserService;


    @PutMapping( "/user/{id}" )
    public ResponseEntity<UserDTO> execute( @RequestBody User user ) {
        return createUserService.execute( user );
    }


}
