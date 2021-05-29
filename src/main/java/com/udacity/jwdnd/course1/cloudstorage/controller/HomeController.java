package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.CredentialForm;
import com.udacity.jwdnd.course1.cloudstorage.model.FileForm;
import com.udacity.jwdnd.course1.cloudstorage.model.NoteForm;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final FileService fileService;
    private final NoteService noteService;
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public HomeController(
            FileService fileService, NoteService noteService,
            CredentialService credentialService, EncryptionService encryptionService) {
        this.fileService = fileService;
        this.noteService = noteService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @GetMapping
    public String getHomePage(@ModelAttribute("newFile") FileForm fileForm,
                              @ModelAttribute("newNote") NoteForm noteForm, @ModelAttribute("newCredential") CredentialForm credentialForm,
                              Model model) {

        model.addAttribute("userFiles", this.fileService.getFileListings());
        model.addAttribute("userNotes", noteService.getNoteListings());
        model.addAttribute("userCredentials", credentialService.getCredentialListings());
        model.addAttribute("encryptionService", encryptionService);

        return "home";
    }

    @PostMapping
    public String newFile(@ModelAttribute("newFile") FileForm fileForm, Model model) throws IOException {

        List<String> fileListings = fileService.getFileListings();
        MultipartFile multipartFile = fileForm.getFile();
        String fileName = multipartFile.getOriginalFilename();

        boolean fileIsDuplicate = false;
        for (String fileListing: fileListings) {
            if (fileListing.equals(fileName)) {
                fileIsDuplicate = true;

                break;
            }
        }
        if (!fileIsDuplicate) {
            fileService.addFile(multipartFile);
            model.addAttribute("result", "success");
        } else {
            model.addAttribute("result", "error");
            model.addAttribute("message", "You have tried to add a duplicate file.");
        }
        model.addAttribute("files", fileService.getFileListings());

        return "result";
    }

    @GetMapping(
            value = "/get-file/{fileName}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody
    byte[] getFile(@PathVariable String fileName) {
        return fileService.getFile(fileName).getFileData();
    }

    @GetMapping(value = "/delete-file/{fileName}")
    public String deleteFile(@PathVariable String fileName, @ModelAttribute("newFile") FileForm newFile,
                             @ModelAttribute("newNote") NoteForm newNote, @ModelAttribute("newCredential") CredentialForm newCredential,
                             Model model) {
        fileService.deleteFile(fileName);

        model.addAttribute("files", fileService.getFileListings());
        model.addAttribute("result", "success");

        return "result";
    }
}
