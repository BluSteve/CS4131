package com.example.logging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("lifecycle","created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("lifecycle", "resumed");
    }
}
