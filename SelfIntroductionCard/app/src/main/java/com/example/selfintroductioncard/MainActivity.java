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
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar.setTitle("Basic Information");
    }

    private void hideInfo() {
        TextView name = (TextView)findViewById(R.id.name);
        name.setVisibility(View.INVISIBLE);
    }
    private void showInfo() {
        TextView name = (TextView)findViewById(R.id.name);
        name.setVisibility(View.VISIBLE);
    }

    private void showCallMe() {
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "(+65)96312156"));
        Button button = findViewById(R.id.callme);
        button.setOnClickListener((s) -> {
            startActivity(i);
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_basicinfo:
                    showCallMe();
                    showInfo();
                    toolbar.setTitle("Basic Information");
                    return true;
                case R.id.navigation_history:
                    toolbar.setTitle("My Life Story");
                    return true;
                case R.id.navigation_hobbies:
                    toolbar.setTitle("Hobbies");
                    return true;
                case R.id.navigation_phone:
                    hideInfo();
                    toolbar.setTitle("Call me!");
                    return true;
            }
            return false;
        }
    };
}
