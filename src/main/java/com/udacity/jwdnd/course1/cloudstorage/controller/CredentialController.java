package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;

@Controller
@RequestMapping("credential")
public class CredentialController {


    private final CredentialService credentialService;
    private final EncryptionService encryptionService;


    public CredentialController(CredentialService credentialService, EncryptionService encryptionService) {
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("add-credential")
    public String addOrUpdateCredential(@ModelAttribute("newCredential") CredentialForm credentialForm, Model model) {

        credentialService.addOrUpdateCredential(credentialForm);

        model.addAttribute("credentials", credentialService.getCredentialListings());
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");

        return "result";
    }

    @GetMapping(value = "/get-credential/{credentialId}")
    public Credential getCredential(@PathVariable Integer credentialId) {
        return credentialService.getCredential(credentialId);
    }

    @GetMapping(value = "/delete-credential/{credentialId}")
    public String deleteCredential(
            Authentication authentication, @PathVariable Integer credentialId,
            @ModelAttribute("newCredential") CredentialForm newCredential,
            @ModelAttribute("newFile") FileForm newFile,
            @ModelAttribute("newNote") NoteForm newNote, Model model) {
        credentialService.deleteCredential(credentialId);
        String userName = authentication.getName();

        model.addAttribute("credentials", credentialService.getCredentialListings());
        model.addAttribute("encryptionService", encryptionService);
        model.addAttribute("result", "success");

        return "result";
    }
}

