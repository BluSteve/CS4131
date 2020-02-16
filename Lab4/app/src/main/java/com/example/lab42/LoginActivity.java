package com.example.lab42;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailText;
    EditText passwordText;
    Button loginBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn = findViewById(R.id.loginBtn);


        mAuth = FirebaseAuth.getInstance();

//        mAuth.createUserWithEmailAndPassword("h1710013@nushigh.edu.sg", "qwertyhi")
//                .addOnCompleteListener(this, (task) ->{
//                    if (task.isSuccessful()) {
//                        Log.d("login", "success");
//                        FirebaseUser user = mAuth.getCurrentUser();
//                    }
//                });


        loginBtn.setOnClickListener((f) -> {
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            Log.d("login", email + ", " + password);
            if (!email.equals("") &&
                    !password.equals("")) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, (s) -> {
                            if (s.isSuccessful()) {
                                Log.d("login", "success");
                                Toast.makeText(LoginActivity.this, "Authentication success!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("admin", true);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("admin", false);
                                startActivity(intent);
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("admin", true);
            startActivity(intent);
        }

    }
}
