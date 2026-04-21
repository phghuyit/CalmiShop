package com.phghuy.calmihome;

import android.content.Intent;
import android.os.Bundle;import android.util.Log;
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
import com.phghuy.calmihome.model.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ApiCaller apiCaller = new ApiCaller(this);

        TextView signUp = findViewById(R.id.regisBtn);
        TextView btn_login = findViewById(R.id.loginBtn);
        TextView forgotpw = findViewById(R.id.forgotPassBtn);

        EditText email_id = findViewById(R.id.emailId);
        EditText password = findViewById(R.id.password);

        forgotpw.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, ForgotPassActivity.class)));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_id.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Bạn chưa nhập Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                apiCaller.Login(email, pass, new ApiCaller.OnRawResponse() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            SessionManager.userId = json.getInt("id");
                            SessionManager.email = json.getString("emailId");
                            SessionManager.password = json.getString("password");

                            Toast.makeText(SignInActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(SignInActivity.this, "Lỗi hệ thống!", Toast.LENGTH_SHORT).show();
                            Log.e("LOGIN", "JSON Error: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SignInActivity.this, "Tài khoản không tồn tại hoặc sai mật khẩu!", Toast.LENGTH_SHORT).show();
                        Log.e("LOGIN", "API Error: " + error);
                    }
                });
            }
        });

        signUp.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUp.class)));
    }
}