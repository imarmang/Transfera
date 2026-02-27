package com.example.transfera.config;

import com.example.transfera.security.UserDetailsServiceImpl;
import com.example.transfera.security.jwt.JwtAuthenticationFilter;
import com.example.transfera.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// THIS CLASS IS THE MANAGER THAT CHECKS IF THE EMAIL EXISTS AND THEN CHECKS FOR THE PASSWORDS
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtil jwtUtil;

    public SecurityConfiguration(UserDetailsServiceImpl userDetailsServiceImpl, JwtUtil jwtUtil) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtUtil = jwtUtil;
    }



    @Bean
    public AuthenticationManager authenticationManagerBean( HttpSecurity http ) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject( AuthenticationManagerBuilder.class );
        builder
                .userDetailsService( userDetailsServiceImpl )  // returns email exists
                .passwordEncoder( passwordEncoder() );  // checks if the passwords match

        return builder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity ) throws Exception {
        return  httpSecurity
                // DISABLE CSRF TO ALLOW POST, PUT, DELETE mappings with authentication
                //.csrf( csrf -> csrf.disable() ) // same thing
                .csrf( AbstractHttpConfigurer::disable )
                .authorizeHttpRequests( authorize -> {
                    authorize.requestMatchers( "/login",
                            "/api/v1/users",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/v3/api-docs/**"
                    ).permitAll();
                    // must be at the bottom
                    authorize.anyRequest().authenticated();

                })
                .addFilterBefore(
                        jwtAuthenticationFilter() ,
                        UsernamePasswordAuthenticationFilter.class )
                .build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter( jwtUtil);
    }
}
