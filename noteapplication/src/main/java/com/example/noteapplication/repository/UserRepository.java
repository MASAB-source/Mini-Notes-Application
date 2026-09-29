package com.example.noteapplication.repository;

import com.example.noteapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    Optional<User> findByProviderIdAndProviderType(String providerId, com.example.noteapplication.models.ProviderType providerType);
}