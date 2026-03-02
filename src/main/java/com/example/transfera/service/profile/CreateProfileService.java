package com.example.transfera.service.profile;


import com.example.transfera.Command;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CreateProfileService implements Command<Void, Void> {


    @Override
    public ResponseEntity<Void> execute(Void input) {
        return null;
    }
}
