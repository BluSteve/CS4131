package com.stevecao.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTravelActivity extends AppCompatActivity {
    EditText destCityText, destCountryText, durationText;
    CalendarView calendarView;
    Button travelSubmitBtn;
    FirebaseFirestore db;
    Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.assignment2.darkmode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_addtravel);
        destCityText = findViewById(R.id.destCityText);
        destCountryText = findViewById(R.id.destCountryText);
        calendarView = findViewById(R.id.calendarView);
        durationText = findViewById(R.id.durationText);
        travelSubmitBtn = findViewById(R.id.travelSubmitBtn);
        db = FirebaseFirestore.getInstance();


        calendarView.setOnDateChangeListener((cv, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            timestamp = new Timestamp(new Date(c.getTimeInMillis()));
        });

        travelSubmitBtn.setOnClickListener((s) -> {
            if (destCityText.getText().toString().equals("") ||
                    destCountryText.getText().toString().equals("") ||
                    durationText.getText().toString().equals("") ||
                    timestamp == null)
                Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_SHORT).show();
            else {
                int duration = Integer.parseInt(durationText.getText().toString());
                String destCity = destCityText.getText().toString();
                String destCountry = destCountryText.getText().toString();
                Map<String, Object> entry = new HashMap<>();
                entry.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                entry.put("duration", duration);
                entry.put("city", destCity);
                entry.put("country", destCountry);
                entry.put("date", timestamp);
                db.collection("travel")
                        .add(entry)
                        .addOnSuccessListener((p) -> {
                            Intent intent = new Intent(AddTravelActivity.this, MainActivity.class);
                            Toast.makeText(this, "Travel declaration recorded!", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        });
            }
        });
    }
}
