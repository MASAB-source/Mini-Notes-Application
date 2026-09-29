package com.example.noteapplication.service;

import com.example.noteapplication.dto.NoteRequest;
import com.example.noteapplication.models.Note;
import com.example.noteapplication.models.User;
import com.example.noteapplication.repository.NoteRepository;
import com.example.noteapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public List<Note> getUserNotes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUser(user);
    }

    public Note createNote(NoteRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setUser(user);

        return noteRepository.save(note);
    }

    public Note updateNote(Long id, NoteRequest request, String email) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to modify this note");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());

        return noteRepository.save(note);
    }

    public void deleteNote(Long id, String email) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getEmail().equals(email)) {
            throw new RuntimeException("Unauthorized to delete this note");
        }

        noteRepository.delete(note);
    }
}