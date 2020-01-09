package com.example.nushscs4203.activitytoactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.NumberFormat;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        String order=intent.getStringExtra("order");
        String total=intent.getStringExtra("total");
        ((TextView)findViewById(R.id.activity_main2_textView1)).setText("You have ordered " +
                order + " coffee. Total is " + NumberFormat.getCurrencyInstance().format(Double.parseDouble(total)));
    }
}
