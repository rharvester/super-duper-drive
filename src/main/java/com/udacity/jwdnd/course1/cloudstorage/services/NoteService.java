package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class NoteService {
    private final AuthenticationService authenticationService;
    private final NoteMapper noteMapper;

    public NoteService( AuthenticationService authenticationService, NoteMapper noteMapper) {
        this.authenticationService = authenticationService;
        this.noteMapper = noteMapper;
    }

    public void addOrUpdateNote(NoteForm noteForm) {
        Note note = new Note();

        note.setNoteTitle(noteForm.getTitle());
        note.setNoteDescription(noteForm.getDescription());
        note.setUserId(authenticationService.getUserId());

        if(noteForm.getNoteId().isEmpty()){
            noteMapper.insert(note);
        } else {
            note.setNoteId(Integer.parseInt(noteForm.getNoteId()));
            noteMapper.updateNote(note);
        }

    }

    public List<Note> getNoteListings() {
        return noteMapper.getNotesForUser(authenticationService.getUserId());
    }

    public Note getNote(Integer noteId) {
        return noteMapper.getNote(noteId);
    }

    public void deleteNote(Integer noteId) {
        noteMapper.deleteNote(noteId);
    }

}

