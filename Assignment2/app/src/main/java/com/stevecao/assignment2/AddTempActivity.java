package com.stevecao.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTempActivity extends AppCompatActivity {
    EditText temperatureText;
    Button tempSubmitBtn;
    ImageView addTempLoadingIV;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.assignment2.darkmode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_addtemp);
        addTempLoadingIV = findViewById(R.id.addTempLoadingIV);
        temperatureText = findViewById(R.id.temperatureText);
        tempSubmitBtn = findViewById(R.id.tempSubmitBtn);
        addTempLoadingIV.bringToFront();
        Glide.with(this).load(R.drawable.loading2).into(addTempLoadingIV);
        db = FirebaseFirestore.getInstance();

        tempSubmitBtn.setOnClickListener((s) -> {
            if (temperatureText.getText().toString().equals(""))
                Toast.makeText(this, "Please enter valid value!", Toast.LENGTH_SHORT).show();
            else {
                double temp = Double.parseDouble(temperatureText.getText().toString());
                if (temp < 34 || temp > 43) {
                    Toast.makeText(this, "I diagnose you with dead. Please measure again.", Toast.LENGTH_LONG).show();
                }
                else {
                    if (temp > 38) {
                        Toast.makeText(this, "You have a fever, please let staff know.", Toast.LENGTH_LONG).show();
                    }
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    entry.put("temp", temp);
                    entry.put("timeTaken", Timestamp.now());


                    addTempLoadingIV.setVisibility(View.VISIBLE);
                    temperatureText.setVisibility(View.GONE);
                    tempSubmitBtn.setVisibility(View.GONE);
                    db.collection("temperatures")
                            .add(entry)
                            .addOnSuccessListener((p) -> {
                                addTempLoadingIV.setVisibility(View.GONE);
                                Intent intent = new Intent(AddTempActivity.this, MainActivity.class);
                                Toast.makeText(this, "Temperature recorded!", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                            });
                }
            }
        });
    }
}
