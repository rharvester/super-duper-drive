package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.mapper.UserMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.CredentialForm;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private final AuthenticationService authenticationService;
    private final EncryptionService encryptionService;
    private final CredentialMapper credentialMapper;

    public CredentialService(AuthenticationService authenticationService, EncryptionService encryptionService, CredentialMapper credentialMapper) {
        this.authenticationService = authenticationService;
        this.encryptionService = encryptionService;
        this.credentialMapper = credentialMapper;
    }

    public void addOrUpdateCredential(CredentialForm credentialForm) {

        Credential credential = new Credential();
        credential.setUrl(credentialForm.getUrl());
        credential.setUserName(credentialForm.getUserName());
        credential.setUserId(authenticationService.getUserId());

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);

        String encryptedPassword = encryptionService.encryptValue(credentialForm.getPassword(), encodedKey);

        credential.setKey(encodedKey);
        credential.setPassword(encryptedPassword);

        if (credentialForm.getCredentialId().isEmpty()) {
            credentialMapper.insert(credential);
        } else {
            credential.setCredentialId(Integer.parseInt(credentialForm.getCredentialId()));
            credentialMapper.updateCredential(credential);
        }

    }

    public List<Credential> getCredentialListings() {
        return credentialMapper.getCredentialListings(authenticationService.getUserId());
    }

    public Credential getCredential(Integer noteId) {
        return credentialMapper.getCredential(noteId);
    }

    public void deleteCredential(Integer noteId) {
        credentialMapper.deleteCredential(noteId);
    }

}
