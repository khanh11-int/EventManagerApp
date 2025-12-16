package com.example.eventmanagerapp.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.usecase.LoginUseCase;
import com.example.eventmanagerapp.presentation.MainActivity;
import com.example.eventmanagerapp.utils.SessionManager;

/**
 * LoginActivity - Màn hình đăng nhập
 */
public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvRegister;

    private LoginUseCase loginUseCase;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check nếu đã đăng nhập → chuyển thẳng vào MainActivity
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void initData() {
        loginUseCase = new LoginUseCase(this);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Xử lý đăng nhập
     */
    private void handleLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();

        // Gọi Use Case
        LoginUseCase.Result result = loginUseCase.execute(username, password);

        if (result.isSuccess()) {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            navigateToMain();
        } else {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Chuyển sang MainActivity
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}