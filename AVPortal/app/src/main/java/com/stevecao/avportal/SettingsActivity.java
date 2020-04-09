package com.stevecao.avportal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.seismic.ShakeDetector;

public class SettingsActivity extends AppCompatActivity {
    SeekBar lightsSeekBar;
    Spinner shakeSpinner;
    TextView noUser, tv, tv2;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_settings);
        lightsSeekBar = findViewById(R.id.lightsSeekBar);
        shakeSpinner = findViewById(R.id.shakeIntensitySpinner);
        noUser = findViewById(R.id.settingsNoUser);
        tv = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            lightsSeekBar.setVisibility(View.GONE);
            shakeSpinner.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            noUser.setVisibility(View.VISIBLE);
        } else {
            lightsSeekBar.setProgress(prefs.getInt("com.stevecao.avportal.faderCount", 5) - 1);
            lightsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    prefs.edit().putInt("com.stevecao.avportal.faderCount", progress + 1).apply();
                    Toast.makeText(SettingsActivity.this, "Fader count set to " + (progress + 1), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.shake_intensities));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            shakeSpinner.setAdapter(adapter);
            int currentI = prefs.getInt("com.stevecao.avportal.shakeIntensity", ShakeDetector.SENSITIVITY_MEDIUM);
            switch (currentI) {
                case ShakeDetector.SENSITIVITY_HARD:
                    shakeSpinner.setSelection(0);
                    break;
                case ShakeDetector.SENSITIVITY_MEDIUM:
                    shakeSpinner.setSelection(1);
                    break;
                case ShakeDetector.SENSITIVITY_LIGHT:
                    shakeSpinner.setSelection(2);
            }
            shakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String choice = parent.getItemAtPosition(position).toString();
                    switch (choice) {
                        case "HARD":
                            prefs.edit().putInt("com.stevecao.avportal.shakeIntensity", ShakeDetector.SENSITIVITY_HARD).apply();
                            break;
                        case "MEDIUM":
                            prefs.edit().putInt("com.stevecao.avportal.shakeIntensity", ShakeDetector.SENSITIVITY_MEDIUM).apply();
                            break;
                        case "LOW":
                            prefs.edit().putInt("com.stevecao.avportal.shakeIntensity", ShakeDetector.SENSITIVITY_LIGHT).apply();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
