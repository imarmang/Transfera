package com.example.transfera.domain.profile;

import com.example.transfera.domain.user.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUserCredentialsEmail( String email );

    // USED TO CHECK IF THE USER ALREADY HAS A PROFILE
    boolean existsByUserCredentials( UserCredentials userCredentials );

    // CHECKING IF THE USERNAME IS TAKEN
    boolean existsByUserName( String userName);

    // SEARCH USERS BY USERNAME — returns top 10 matches starting with the given string
    List<Profile> findTop10ByUserNameStartingWithIgnoreCase( String userName );
}
