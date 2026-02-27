package com.example.transfera.security;

import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialsRepository userCredentialsRepository;

    public UserDetailsServiceImpl( UserCredentialsRepository userCredentialsRepository ) {
        this.userCredentialsRepository = userCredentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException {

        // CHECK IF THE EMAIL EXISTS
        UserCredentials userCredentials = userCredentialsRepository.findByEmail( username )
                .orElseThrow( () -> new UsernameNotFoundException( username ) );

        // BUILD A SPRING SECURITY USER OBJECT
        return User.builder()
                .username( userCredentials.getEmail() )
                .password( userCredentials.getPassword() )
                .build();
    }
}
