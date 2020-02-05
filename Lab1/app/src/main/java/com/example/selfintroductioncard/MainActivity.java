package com.example.selfintroductioncard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActionBar toolbar;
    TextView name;
    EditText infoText, hobbyText;
    ImageView me,keyboard;
    Button callme, backstory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        infoText = findViewById(R.id.infoText);
        me = findViewById(R.id.pictureOfMe);
        callme = findViewById(R.id.callme);
        hobbyText = findViewById(R.id.hobbyText);
        backstory = findViewById(R.id.backstory);
        keyboard = findViewById(R.id.pictureOfKeyboard);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Information");
        callMe();
        hideHobby();

        infoText.setText("Class: M20404\n" +
                "Phone number: 96312156\n" +
                "Email: h1710013@nushigh.edu.sg\n\n" +
                "Hi! My name is Steve and I am the only one in this class who doesn't take " +
                "physics. Yes, my life is so boring that this counts as a fun fact.\n\n" +
                "Here's a photo of me on the one occasion I actually did something interesting " +
                "(visiting Huangshan).");
        hobbyText.setText("As you may have deduced from the \"Info page\", I enjoy travelling, " +
                        "especially to exotic places where there are few people. I see enough " +
                        "people in Singapore, so when I travel I try to go to serene, quiet places. " +
                        "\n\nIn my spare time, I like to improve my Rubik's cube time (currently at sub" +
                        "-30 but is still improving) and tinkering about with computers. Currently " +
                        "some of my projects include learning Linux, setting up a VPN that isn't " +
                        "blocked in China and building a mechanical keyboard.");
        infoText.setFocusable(false);
        hobbyText.setFocusable(false);

        backstory.setOnClickListener((s) -> {
            Intent i = new Intent(MainActivity.this, BackstoryActivity.class);
            startActivity(i);
        });
    }

    private void hideInfo() {
        name.setVisibility(View.INVISIBLE);
        infoText.setVisibility(View.INVISIBLE);
        me.setVisibility(View.INVISIBLE);
        callme.setVisibility(View.INVISIBLE);
    }
    private void showInfo() {
        name.setVisibility(View.VISIBLE);
        infoText.setVisibility(View.VISIBLE);
        me.setVisibility(View.VISIBLE);
        callme.setVisibility(View.VISIBLE);
    }

    private void callMe() {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "(+65)96312156"));
        callme.setOnClickListener((s) -> {
            startActivity(i);
        });
    }

    private void showHobby() {
        hobbyText.setVisibility(View.VISIBLE);
        backstory.setVisibility(View.VISIBLE);
        keyboard.setVisibility(View.VISIBLE);
    }

    private void hideHobby() {
        hobbyText.setVisibility(View.INVISIBLE);
        backstory.setVisibility(View.INVISIBLE);
        keyboard.setVisibility(View.INVISIBLE);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_basicinfo:
                    callMe();
                    showInfo();
                    hideHobby();
                    toolbar.setTitle("Information");
                    return true;
                case R.id.navigation_hobbies:
                    hideInfo();
                    showHobby();
                    toolbar.setTitle("Hobbies");
                    return true;
            }
            return false;
        }
    };
}
