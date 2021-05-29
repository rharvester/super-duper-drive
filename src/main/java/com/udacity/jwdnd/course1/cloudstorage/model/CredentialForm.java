package com.udacity.jwdnd.course1.cloudstorage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialForm {
    private String credentialId;
    private String url;
    private String userName;
    private String password;
}
