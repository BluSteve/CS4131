package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.lab3.ui.login.LoginActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity{
    Button b;
    GridView gv;
    private static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new File(MyApplication.getAppContext().getExternalFilesDir(null),"login.txt");

//        try{
//            Log.d("login", ""+file.createNewFile()+" " + MyApplication.getAppContext().getExternalFilesDir(null));}catch(Exception e){}
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gridView);
        b = findViewById(R.id.login);
        b.setVisibility(View.VISIBLE);
        gv.setVisibility(View.GONE);
        gv.setAdapter(new ImageAdapter(this));

        b.setOnClickListener((s) -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
                });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "Image Position: " + position,
                            Toast.LENGTH_SHORT).show();

            }
        });
    }
    protected void onResume() {
        super.onResume();
        try {
            String s = getIntent().getExtras().getString("message");
            if (s.equals("logged in")) {
                b.setVisibility(View.GONE);
                gv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }
    public static Context getAppContext() {
        return MainActivity.context;
    }
}
