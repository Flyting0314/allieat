package com.storeOrder.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Service
public class FirebaseAuthService {

    private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String SERVICE_ACCOUNT_FILE = "src/main/resources/serviceAccountKey.json"; 

    public String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_FILE))
                .createScoped(Collections.singleton(SCOPE));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
