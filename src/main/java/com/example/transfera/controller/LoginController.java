//package com.example.transfera.controller;
//
//import com.example.transfera.security.CustomUser;
//import com.example.transfera.security.jwt.JwtUtil;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class LoginController {
//    private final AuthenticationManager manager;
//
//
//    public LoginController( AuthenticationManager manager ) {
//        this.manager = manager;
//    }
//
//    @PostMapping( "/login" )
//    public ResponseEntity<String> login( @RequestBody CustomUser user ) {
//
//        // this token is different from JWT json web token
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                user.getUsername(),
//                user.getPassword()
//        );
//
//        // this will fault if the credentials are not valid
//        Authentication authentication = manager.authenticate( token );
//
//        SecurityContextHolder.getContext().setAuthentication( authentication );
//
//        String jwtToken = JwtUtil.generateToken( (User) authentication.getPrincipal() );
//        return ResponseEntity.ok( jwtToken );
//    }
//
//}
