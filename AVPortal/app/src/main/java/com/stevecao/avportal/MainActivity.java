package com.stevecao.avportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    TextView nameTV, emailTV;
    ImageView pfpIV;
    Button loginBtn, signUpBtn;
    LinearLayout navLL;
    Toolbar toolbar;
    private AppBarConfiguration mAppBarConfiguration;

    private void updateViews() {
        loginBtn.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(View.VISIBLE);
        emailTV.setVisibility(View.GONE);
        nameTV.setVisibility(View.GONE);
        pfpIV.setVisibility(View.GONE);
        navLL.setGravity(Gravity.CENTER);
        loginBtn.setOnClickListener((s) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        signUpBtn.setOnClickListener((s) -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener((item) -> {
            Log.d("ddd", "ddd");
            switch (item.getItemId()) {
                case R.id.action_settings:
                    // TODO change to settings activity
                    break;
                case R.id.action_logout:
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Sign-out successful!", Toast.LENGTH_SHORT).show();
                    updateViews();
            }
            return false;
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nameTV = headerView.findViewById(R.id.nameTV);
        emailTV = headerView.findViewById(R.id.emailTV);
        loginBtn = headerView.findViewById(R.id.loginBtn);
        signUpBtn = headerView.findViewById(R.id.signUpBtn);
        pfpIV = headerView.findViewById(R.id.pfpIV);
        navLL = headerView.findViewById(R.id.navLL);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("login", FirebaseAuth.getInstance().getCurrentUser() + "");
            loginBtn.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.GONE);

            FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
            emailTV.setText(cUser.getEmail());
            nameTV.setText(prefs.getString("name", cUser.getEmail().split("@")[0]));
            navLL.setGravity(Gravity.BOTTOM);

            emailTV.setVisibility(View.VISIBLE);
            nameTV.setVisibility(View.VISIBLE);
            pfpIV.setVisibility(View.VISIBLE);
        } else {
            updateViews();
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_announcements, R.id.nav_events, R.id.nav_equipment)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toolbar. != null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                toolbar.getMenu().findItem(R.id.action_logout).setVisible(true);
            } else {
                toolbar.getMenu().findItem(R.id.action_logout).setVisible(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            menu.findItem(R.id.action_logout).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
