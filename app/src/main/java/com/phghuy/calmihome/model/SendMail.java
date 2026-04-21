package com.phghuy.calmihome.model;

import android.util.Log;
import android.util.Patterns;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMail {
    private static final String SENDGRID_API_KEY = "dien key o day";
    private static final String FROM_EMAIL = "dien mail o day";

    public static void sendEmail(String toEmailRaw, String password) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();

            // Làm sạch email
            String toEmail = toEmailRaw.trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(toEmail).matches()) {
                Log.e("SendGrid", "Invalid email address: " + toEmail);
                return;
            }

            String jsonBody = "{"
                    + "\"personalizations\": [{\"to\": [{\"email\": \"" + toEmail + "\"}] }],"
                    + "\"from\": { \"email\": \"" + FROM_EMAIL + "\" },"
                    + "\"subject\": \"Mật khẩu mới của Calmi Shop\","
                    + "\"content\": [{\"type\": \"text/plain\", \"value\": \"Mật khẩu mới của bạn là: " + password + "\" }]"
                    + "}";

            Request request = new Request.Builder()
                    .url("https://api.sendgrid.com/v3/mail/send")
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                    .addHeader("Authorization", "Bearer " + SENDGRID_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    Log.e("SendGrid", "Error sending: " + response.code() + " - " + response.body().string());
                } else {
                    Log.d("SendGrid", "Email sent successfully!");
                }
            } catch (IOException e) {
                Log.e("SendGrid", "Exception: ", e);
            }
        }).start();
    }
}
