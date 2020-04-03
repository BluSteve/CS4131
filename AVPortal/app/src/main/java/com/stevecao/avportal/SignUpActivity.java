package com.stevecao.avportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    EditText emailText, passwordText, nameText, phoneText;
    Button submitBtn;
    ImageView loginLoadingIV;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_login);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        submitBtn = findViewById(R.id.submitBtn);
        loginLoadingIV = findViewById(R.id.loginLoadingIV);
        phoneText = findViewById(R.id.phoneText);
        mAuth = FirebaseAuth.getInstance();

        nameText.setVisibility(View.VISIBLE);
        phoneText.setVisibility(View.VISIBLE);
        submitBtn.setText(getString(R.string.signUpBtn));
        Glide.with(this).load(R.drawable.loading2).into(loginLoadingIV);

        submitBtn.setOnClickListener((s) -> {
            if (emailText.getText().toString().equals("") ||
                    passwordText.getText().toString().equals("") ||
                    nameText.getText().toString().equals("") ||
                    phoneText.getText().toString().equals("") ||
                    phoneText.getText().toString().length() < 8)
                Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_SHORT).show();
            else {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String name = nameText.getText().toString();
                String phone = phoneText.getText().toString();
                String emailDomain = email.substring(email.indexOf('@') + 1);
                if (emailDomain.equals("nus.edu.sg") || emailDomain.equals("nushigh.edu.sg")) {
                    if (password.length() >= 8) {
                        loginLoadingIV.setVisibility(View.VISIBLE);
                        emailText.setVisibility(View.GONE);
                        submitBtn.setVisibility(View.GONE);
                        passwordText.setVisibility(View.GONE);
                        nameText.setVisibility(View.GONE);
                        phoneText.setVisibility(View.GONE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("isAdmin", false);
                                            hashMap.put("isCrew", false);
                                            hashMap.put("isTeacherIc", true);
                                            hashMap.put("name", name);
                                            hashMap.put("email", email);
                                            hashMap.put("number", phone);
                                            FirebaseFirestore.getInstance().collection("users")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .set(hashMap)
                                                    .addOnSuccessListener((task2) -> {
                                                        loginLoadingIV.setVisibility(View.GONE);
                                                        Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                    });

                                        } else {
                                            loginLoadingIV.setVisibility(View.GONE);
                                            emailText.setVisibility(View.VISIBLE);
                                            submitBtn.setVisibility(View.VISIBLE);
                                            nameText.setVisibility(View.VISIBLE);
                                            passwordText.setVisibility(View.VISIBLE);
                                            Toast.makeText(SignUpActivity.this, "Sign-up failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Password too short!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Enter valid NUSH email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
