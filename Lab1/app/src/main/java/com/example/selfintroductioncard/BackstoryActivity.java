package com.example.selfintroductioncard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class BackstoryActivity extends AppCompatActivity {
    EditText backstory;
    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backstory_main);
        backstory = findViewById(R.id.backstory);
        goBack = findViewById(R.id.goBack);

        getSupportActionBar().setTitle("Backstory");

        backstory.setText("I was born in Beijing but I came to Singapore at the age of 7. " +
                "Didn't speak a word of English back then so not a single MOE school would accept " +
                "me. So I went to Canadian International School and learned \"authentic\" English. " +
                "Then in 2016, forbidden to take PSLE, I took 6 DSA exams and got into this school " +
                "and River Valley.");
        backstory.setFocusable(false);

        goBack.setOnClickListener((s) -> {
            Intent i = new Intent(BackstoryActivity.this, MainActivity.class);
            startActivity(i);
        });
    }
}
