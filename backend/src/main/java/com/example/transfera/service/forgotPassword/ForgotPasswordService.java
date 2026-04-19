package com.example.transfera.service.forgotPassword;

import com.example.transfera.domain.password_reset.PasswordResetRepository;
import com.example.transfera.domain.password_reset.PasswordResetToken;
import com.example.transfera.domain.user.UserCredentials;
import com.example.transfera.domain.user.UserCredentialsRepository;
import com.example.transfera.dto.AuthDTO.forgotPassword.ForgotPasswordResponseDTO;
import com.example.transfera.exceptions.customExceptions.UserNotFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;


// TODO: Takes an email
//  Generates a reset token
//  Saves it to the DB
//  Sends the email with the reset link
@Service
public class ForgotPasswordService {

    private final UserCredentialsRepository userCredentialsRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final JavaMailSender mailSender;
    @Value("${app.frontend.url}")
    private String frontEndUrl;

    @Value("${app.mail.from}")
    private String from;


    public ForgotPasswordService( UserCredentialsRepository userCredentialsRepository,
                                  PasswordResetRepository passwordResetRepository,
                                  JavaMailSender mailSender ) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.mailSender = mailSender;
    }

    public ForgotPasswordResponseDTO execute( String email ) {
        System.out.println( "ForgotPasswordService: received request for email: " + email );

        Optional<UserCredentials> userOpt = userCredentialsRepository.findByEmail( email );

        // Check if the user exists
        if ( userOpt.isEmpty() ) {
            System.out.println("ForgotPasswordService: no user found with email: " + email );
            throw new UserNotFound();
        }

        UserCredentials user = userOpt.get();

        // Create a temporary token
        PasswordResetToken passwordResetToken = new PasswordResetToken( user );

        passwordResetRepository.save( passwordResetToken );
        System.out.println("ForgotPasswordService: token saved: " + passwordResetToken.getToken() );

        String resetLink = frontEndUrl + "/reset_password?token=" + passwordResetToken.getToken();
        System.out.println( "ForgotPasswordService: reset link generated: " + resetLink );

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom( from );
        mailMessage.setTo( email );
        mailMessage.setSubject( "Reset your Transfera password" );

        mailMessage.setText( "Hi,\n\nClick the link below to reset your password. This link expires in 15 minutes.\n\n"
                + resetLink
                + "\n\nIf you did not request this, please ignore this email." );

        mailSender.send( mailMessage );

        System.out.println( "ForgotPasswordService: email sent successfully to: " + email );
        return new ForgotPasswordResponseDTO("Password reset link sent to " + email);
    }

}
