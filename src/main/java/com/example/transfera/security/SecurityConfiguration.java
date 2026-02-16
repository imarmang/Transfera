package com.example.transfera.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public AuthenticationManager authenticationManagerBean( HttpSecurity http ) throws Exception {
        return http.getSharedObject( AuthenticationManagerBuilder.class ).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity ) throws Exception {
        return  httpSecurity
                // DISABLE CSRF TO ALLOW POST, PUT, DELETE mappings with authentication
//                .csrf( csrf -> csrf.disable() ) // same thing
                .csrf( AbstractHttpConfigurer::disable )
                .authorizeHttpRequests( authorize -> {

                    // have to let user create new without valid credentials
                    authorize.requestMatchers( "/createnewuser" ).permitAll();

                    // must be at the bottom
                    authorize.anyRequest().authenticated();

                })
                .addFilterBefore(
                        new BasicAuthenticationFilter( authenticationManagerBean( httpSecurity ) ),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
