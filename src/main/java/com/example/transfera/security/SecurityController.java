package com.example.transfera.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping( "/open" )
    public String open() {
        return "OPEN";
    }

    @GetMapping( "/closed" )
    public String closed() {
        return "CLOSED";
    }

    @GetMapping( "/special" )
    public String special() {
        return "SPECIAL";
    }

    @GetMapping( "/basic" )
    public String basic() {
        return "BASIC";
    }
}
