package com.stevecao.avportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stevecao.avportal.model.User;

import androidx.core.widget.ImageViewCompat;
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
    ImageButton darkModeBtn;
    LinearLayout navLL;
    Toolbar toolbar;
    NavController navController;
    private static User user;
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
        if (toolbar.getMenu().findItem(R.id.action_logout) != null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                toolbar.getMenu().findItem(R.id.action_logout).setVisible(true);
            } else {
                toolbar.getMenu().findItem(R.id.action_logout).setVisible(false);
            }
        }
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainActivity.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nameTV = headerView.findViewById(R.id.nameTV);
        emailTV = headerView.findViewById(R.id.emailTV);
        loginBtn = headerView.findViewById(R.id.loginBtn);
        signUpBtn = headerView.findViewById(R.id.signUpBtn);
        darkModeBtn = navigationView.findViewById(R.id.darkModeBtn);
        pfpIV = headerView.findViewById(R.id.pfpIV);
        navLL = headerView.findViewById(R.id.navLL);

        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true)) {
            ImageViewCompat.setImageTintList(darkModeBtn, ColorStateList.valueOf(getColor(R.color.white)));
            darkModeBtn.setImageResource(R.drawable.ic_moon_black_24dp);
        } else if (!prefs.getBoolean("com.stevecao.avportal.darkMode", true)) {
            darkModeBtn.setImageResource(R.drawable.ic_sun_black_24dp);
            ImageViewCompat.setImageTintList(darkModeBtn, ColorStateList.valueOf(getColor(R.color.black)));
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("login", FirebaseAuth.getInstance().getCurrentUser() + "");
            loginBtn.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.GONE);

            FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(cUser.getUid())
                    .get()
                    .addOnSuccessListener((task) -> {
                        user = new User(task.get("name").toString(), task.get("number").toString(),
                                task.get("email").toString());

                        emailTV.setText(cUser.getEmail());
                        nameTV.setText(user.getName());
                        navLL.setGravity(Gravity.BOTTOM);

                        emailTV.setVisibility(View.VISIBLE);
                        nameTV.setVisibility(View.VISIBLE);
                        pfpIV.setVisibility(View.VISIBLE);
                    });
        } else {
            updateViews();
        }

        darkModeBtn.setOnClickListener((s) -> {
            boolean isDark = prefs.getBoolean("com.stevecao.avportal.darkMode", true);
            if (isDark) {
                prefs.edit().putBoolean("com.stevecao.avportal.darkMode", false).apply();
                isDark = prefs.getBoolean("com.stevecao.avportal.darkMode", true);
                darkModeBtn.setImageResource(R.drawable.ic_sun_black_24dp);
            } else {
                prefs.edit().putBoolean("com.stevecao.avportal.darkMode", true).apply();
                darkModeBtn.setImageResource(R.drawable.ic_moon_black_24dp);
            }
            Toast.makeText(this, "Restart app to see changes.", Toast.LENGTH_SHORT).show();

        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_announcements, R.id.nav_events, R.id.nav_equipment, R.id.nav_lights, R.id.nav_resources)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        toolbar.setOnMenuItemClickListener((item) -> {
            Log.d("ddd", "ddd");
            switch (item.getItemId()) {
                case R.id.action_settings:
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.action_logout:
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Sign-out successful!", Toast.LENGTH_SHORT).show();
                    finish();
                    getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(getIntent());
                    updateViews();
                    break;
                case R.id.action_stageMode:
                    //TODO add stageMode
                    Intent intent2 = new Intent(this, StageModeActivity.class);
                    startActivity(intent2);
            }
            return false;
        });

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toolbar.getMenu().findItem(R.id.action_logout) != null) {
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
