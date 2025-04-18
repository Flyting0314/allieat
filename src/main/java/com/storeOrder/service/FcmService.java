package com.storeOrder.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class FcmService {

    @Autowired
    private FirebaseAuthService firebaseAuthService;

    private static final String PROJECT_ID = "allieat-notification"; // 你的Firebase專案ID

    public void sendNotification(String fcmToken, String title, String body) {
        try {
            String accessToken = firebaseAuthService.getAccessToken();
            URL url = new URL("https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json; UTF-8");
            conn.setDoOutput(true);

            String payload = """
                {
                  "message": {
                    "token": "%s",
                    "notification": {
                      "title": "%s",
                      "body": "%s"
                    }
                  }
                }
                """.formatted(fcmToken, title, body);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("📨 FCM推播回應：" + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
