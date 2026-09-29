package com.example.noteapplication.repository;

import com.example.noteapplication.models.RefreshToken;
import com.example.noteapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import java.util.Optional;

//@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    void deleteByUser(User user);
}