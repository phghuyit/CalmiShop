package com.phghuy.calmihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.User;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText fullNameET = findViewById(R.id.fullName);
        EditText emailIdET = findViewById(R.id.emailId);
        EditText passwordET = findViewById(R.id.password);
        EditText confirmPasswordET = findViewById(R.id.confirmpassword);

        TextView signUp = findViewById(R.id.signUpBtn);
        TextView regisBtn = findViewById(R.id.regisBtn);

        ApiCaller apiCaller = new ApiCaller(this);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, SignInActivity.class);
            startActivity(intent);
        });

        regisBtn.setOnClickListener(v -> {
            String email = emailIdET.getText().toString().trim();
            String pass = passwordET.getText().toString().trim();
            String confirm = confirmPasswordET.getText().toString().trim();
            String fullName = fullNameET.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(SignUp.this, "Bạn cần nhập Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty()) {
                Toast.makeText(SignUp.this, "Password không được rỗng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(SignUp.this, "Passwords không trùng khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User();
            user.setFirstName(fullName); // Tạm thời để fullName vào firstName
            user.setEmailId(email);
            user.setPassword(pass);
            user.setActive(true);
            user.setId(0);

            apiCaller.registerUser(user, new ApiCaller.OnRawResponse() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(SignUp.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình đăng ký
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(SignUp.this, "Đăng ký thất bại: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}