package com.example.noteapplication.controller;

import com.example.noteapplication.dto.NoteRequest;
import com.example.noteapplication.models.Note;
import com.example.noteapplication.service.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> getNotes(Authentication auth) {
        return noteService.getUserNotes(auth.getName());
    }

    @PostMapping
    public Note create(@RequestBody NoteRequest request, Authentication auth) {
        return noteService.createNote(request, auth.getName());
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable Long id, @RequestBody NoteRequest request, Authentication auth) {
        return noteService.updateNote(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        noteService.deleteNote(id, auth.getName());
    }
}