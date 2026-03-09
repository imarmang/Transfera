package com.example.transfera.service.forgotPassword;

// TODO: Takes the token + new password
//  Validates the token (exists, not expired, not used)
//  Updates the password
//  Marks the token as used

import com.example.transfera.domain.password_reset.PasswordResetRepository;
import com.example.transfera.domain.password_reset.PasswordResetToken;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.AuthDTO.resetPassword.ResetPasswordResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.transfera.exceptions.InvalidResetTokenException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResetPasswordService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService( PasswordResetRepository passwordResetRepository,
                                 UserCredentialsRepository userCredentialsRepository,
                                 PasswordEncoder passwordEncoder ) {
        this.passwordResetRepository = passwordResetRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResetPasswordResponseDTO execute( String token, String newPassword ) {
        System.out.println( "REsetPasswordService: received reset request for token: " + token );

        Optional<PasswordResetToken> tokenOpt = passwordResetRepository.findByToken( token );

        if ( tokenOpt.isEmpty() ) {
            System.out.println( "ResetPasswordService: token not found" );
            throw new InvalidResetTokenException();
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if ( resetToken.isExpired() ) {
            System.out.println( "ResetPasswordService: token is expired" );
            throw new InvalidResetTokenException();
        }

        if ( resetToken.isUsed() ) {
            System.out.println( "ResetPasswordService: token is used" );
            throw new InvalidResetTokenException();
        }

        UserCredentials user = resetToken.getUser();
        user.setPassword( passwordEncoder.encode( newPassword ) );

        userCredentialsRepository.save( user );
        System.out.println( "ResetPasswordService: password updated for user: " + user );

        resetToken.setUsed( true );
        passwordResetRepository.save( resetToken );

        System.out.println( "ResetPasswordService: password updated for user: " + user );
        return new ResetPasswordResponseDTO( "Password reset successfully" );

    }
}
