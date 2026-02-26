package com.example.transfera.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<User, UUID> {}
