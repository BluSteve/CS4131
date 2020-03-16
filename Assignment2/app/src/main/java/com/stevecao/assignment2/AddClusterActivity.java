package com.stevecao.assignment2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.stevecao.assignment2.model.Cluster;
import com.stevecao.assignment2.model.ClusterStorage;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddClusterActivity extends AppCompatActivity {
    EditText locationText, casesText;
    Button clusterSubmitBtn;
    Spinner clusterSpinner;
    ImageView loading;
    ArrayList<String> clusterNames = new ArrayList<>(0);
    FirebaseFirestore db;
    String addClusterHint;
    Context context;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.assignment2.darkmode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_addcluster);
        locationText = findViewById(R.id.locationText);
        casesText = findViewById(R.id.casesText);
        clusterSubmitBtn = findViewById(R.id.clusterSubmitBtn);
        clusterSpinner = findViewById(R.id.clusterSpinner);
        addClusterHint = getString(R.string.addClusterHint);
        loading = findViewById(R.id.addClusterLoadingIV);
        context = getApplicationContext();
        db = FirebaseFirestore.getInstance();

        Glide.with(this).load(R.drawable.loading2).into(loading);


        clusterNames.add(addClusterHint);
        for (Cluster c : ClusterStorage.getClusters()) {
            clusterNames.add(c.getName());
        }
        Collections.sort(clusterNames);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                clusterNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clusterSpinner.setAdapter(spinnerAdapter);

        clusterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals(addClusterHint)) { // add new cluster
                    locationText.setVisibility(View.VISIBLE);
                    clusterSubmitBtn.setOnClickListener((s) -> {
                        String location = locationText.getText().toString();
                        String casesStr = casesText.getText().toString();
                        if (location.equals("") ||
                                casesStr.equals(""))
                            Toast.makeText(context, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                        else {
                            GeoPoint coords = Cluster.getCoords(location);
                            if (coords != null) {
                                if (ClusterStorage.getClusterByGeoPoint(coords) == null) {
                                    clusterSpinner.setVisibility(View.GONE);
                                    locationText.setVisibility(View.GONE);
                                    casesText.setVisibility(View.GONE);
                                    clusterSubmitBtn.setVisibility(View.GONE);
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("name", location);
                                    data.put("cases", Integer.parseInt(casesStr));
                                    db.collection("clusters")
                                            .add(data)
                                            .addOnCompleteListener((task) -> {
                                                loading.setVisibility(View.GONE);
                                                Toast.makeText(context, "Cluster added!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(context, MainActivity.class));
                                            });
                                } else {
                                    Toast.makeText(context, "Cluster already present!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "Please enter valid location!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else { // update existing cluster
                    locationText.setVisibility(View.GONE);
                    clusterSubmitBtn.setOnClickListener((s) -> {
                        String casesStr = casesText.getText().toString();
                        if (casesStr.equals(""))
                            Toast.makeText(context, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                        else {
                            loading.setVisibility(View.VISIBLE);
                            clusterSpinner.setVisibility(View.GONE);
                            casesText.setVisibility(View.GONE);
                            clusterSubmitBtn.setVisibility(View.GONE);
                            Log.d("clusterlocation", Cluster
                                    .getCoords(parent.getItemAtPosition(position)
                                            .toString()).toString());
                            Log.d("clusterlocation", ClusterStorage.getClusters().toString());
                            String documentId = ClusterStorage.getClusterByGeoPoint(Cluster
                                    .getCoords(parent.getItemAtPosition(position)
                                            .toString())).getDocumentId();
                            Log.d("locations", documentId);
                            db.collection("clusters").document(documentId)
                                    .update("cases", Integer.parseInt(casesStr))
                                    .addOnCompleteListener((task) -> {
                                        loading.setVisibility(View.GONE);
                                        Toast.makeText(context, "Cluster updated!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(context, MainActivity.class));
                                    });
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
}
}
