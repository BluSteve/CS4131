package com.stevecao.avportal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    SeekBar lightsSeekBar;
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
        lightsSeekBar.setProgress(prefs.getInt("com.stevecao.avportal.faderCount", 5)-1);
        lightsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefs.edit().putInt("com.stevecao.avportal.faderCount", progress+1).apply();
                Toast.makeText(SettingsActivity.this, "Fader count set to " +  (progress + 1), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
