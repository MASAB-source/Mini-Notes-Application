package com.example.noteapplication.repository;

import com.example.noteapplication.models.Note;
import com.example.noteapplication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    List<Note> findByUser(User user);
    
    List<Note> findByUserId(Long userId);
}