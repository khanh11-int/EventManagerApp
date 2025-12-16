package com.example.eventmanagerapp.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanagerapp.R;
import com.example.eventmanagerapp.domain.usecase.RegisterUseCase;
import com.example.eventmanagerapp.presentation.MainActivity;

/**
 * RegisterActivity - Màn hình đăng ký
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    private RegisterUseCase registerUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void initData() {
        registerUseCase = new RegisterUseCase(this);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());

        tvLogin.setOnClickListener(v -> finish());
    }

    /**
     * Xử lý đăng ký
     */
    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        // Gọi Use Case
        RegisterUseCase.Result result = registerUseCase.execute(
                username, password, confirmPassword, fullName
        );

        if (result.isSuccess()) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
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