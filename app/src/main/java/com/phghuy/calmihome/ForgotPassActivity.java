package com.phghuy.calmihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.SendMail;
import com.phghuy.calmihome.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class ForgotPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ApiCaller apiCaller = new ApiCaller(this);
        TextView toCreatePassBtn = findViewById(R.id.toCreatePassBtn);
        ImageView backBtn = findViewById(R.id.backBtn);
        EditText emailid = findViewById(R.id.emailId);

        backBtn.setOnClickListener(v -> finish());

        toCreatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailid.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPassActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Tìm user theo email
                apiCaller.getItems("/user/find/" + email, null, new ApiCaller.OnRawResponse() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            int iduser = json.getInt("id");
                            String newPassword = generateRandomPassword(6);

                            // Tạo object user để update
                            User user_update = new User();
                            user_update.setId(iduser);
                            user_update.setEmailId(email);
                            user_update.setPassword(newPassword);
                            // Giữ lại các thông tin cũ từ server
                            user_update.setLastName(json.optString("lastName", ""));
                            user_update.setFirstName(json.optString("firstName", ""));
                            user_update.setUserName(json.optString("userName", ""));
                            user_update.setActive(json.optBoolean("active", true));

                            // 2. Cập nhật mật khẩu mới lên Server
                            apiCaller.updateUser(user_update, new ApiCaller.OnRawResponse() {
                                @Override
                                public void onSuccess(String response) {
                                    // 3. Gửi email thông báo mật khẩu mới
                                    SendMail.sendEmail(email, newPassword);
                                    Toast.makeText(ForgotPassActivity.this, "Đã gửi mật khẩu qua email!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(ForgotPassActivity.this, "Lỗi hệ thống khi cập nhật!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (JSONException e) {
                            Toast.makeText(ForgotPassActivity.this, "Lỗi đọc dữ liệu!", Toast.LENGTH_SHORT).show();
                            Log.e("FORGOT", "JSON Error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ForgotPassActivity.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Hàm tạo mật khẩu ngẫu nhiên 6 chữ số
    public static String generateRandomPassword(int length) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
