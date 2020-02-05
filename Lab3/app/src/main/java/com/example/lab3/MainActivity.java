package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.lab3.ui.login.LoginActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static Context context;
    Button b;
    GridView gv;
    RatingBar rb;
    String imgname;
    String username;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gridView);
        b = findViewById(R.id.login);
        rb = findViewById(R.id.ratingBar);

//        try {
//            File file = new File(getExternalFilesDir(null), "ratings.txt");
//            PrintWriter out2 = new PrintWriter(new FileWriter(file));
//            out2.print("");
//        } catch (Exception e){}

        b.setVisibility(View.VISIBLE);
        gv.setVisibility(View.GONE);
        rb.setVisibility(View.GONE);
        gv.setAdapter(new ImageAdapter(this));

        b.setOnClickListener((s) -> {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        });
        gv.setOnItemClickListener((a, b, position, d) -> {
            rb.setVisibility(View.VISIBLE);

            Toast t = Toast.makeText(MainActivity.this, "Image Position: " + position,
                    Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();

            imgname = "img" + position;
        });
        rb.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating != 0.0) {
                try {
                    File file = new File(getExternalFilesDir(null), "ratings.txt");
                    Log.d("rating", "" + file.createNewFile());

                    Log.d("rating", rating + "");

                    PrintWriter out = new PrintWriter(new FileWriter(file, true));
                    long millis = System.currentTimeMillis();
                    Date date = new Date(millis);
                    out.println(username + "," + imgname + "," + rating + "," + millis + "," + date);
                    out.close();

                    Toast.makeText(MainActivity.this, "Rating Recorded!",
                            Toast.LENGTH_SHORT).show();

                    rb.setVisibility(View.INVISIBLE);
                    rb.setRating(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        try {
            String s = getIntent().getExtras().getString("message");
            if (s.equals("logged in")) {
                username = getIntent().getExtras().getString("username");
                b.setVisibility(View.GONE);
                gv.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }
}
