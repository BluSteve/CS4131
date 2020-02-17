package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    ImageView photoIV;
    Button addPhotoBtn;
    Button submitBtn;
    Button resetBtn;
    RadioGroup faultLocationRG;
    Spinner faultNatureSpinner;
    Spinner levelSpinner;
    Spinner blockSpinner;
    String[] faultLocations = new String[]{"Bathroom", "Classroom", "Stairwell/Hallway"};
    String faultLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoIV = findViewById(R.id.photoIV);
        addPhotoBtn = findViewById(R.id.addPhotoBtn);
        faultLocationRG = findViewById(R.id.faultLocationRG);
        faultNatureSpinner = findViewById(R.id.faultNatureSpinner);
        levelSpinner = findViewById(R.id.levelSpinner);
        blockSpinner = findViewById(R.id.blockSpinner);
        submitBtn = findViewById(R.id.submitBtn);
        resetBtn = findViewById(R.id.resetBtn);

        faultNatureSpinner.setVisibility(View.GONE);
        hideBLS();
        




        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this, 
            R.array.levels, android.R.layout.simple_spinner_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> blockAdapter = ArrayAdapter.createFromResource(this,
                R.array.blocks, android.R.layout.simple_spinner_item);
        blockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        

        for (String f : faultLocations) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(View.generateViewId());
            radioButton.setText(f);
            faultLocationRG.addView(radioButton);
        }


        faultLocationRG.setOnCheckedChangeListener((radioGroup, listener) -> {
            int selectedId = faultLocationRG.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            faultLocation = rb.getText().toString();
            faultNatureSpinner.setVisibility(View.VISIBLE);

            int chosenArrayId = R.array.bathroom_faults;
            switch (faultLocation) {
                case "Bathroom":
                    chosenArrayId = R.array.bathroom_faults;
                    break;
                case "Classroom":
                    chosenArrayId = R.array.classroom_faults;
                    break;
                case "Stairwell/Hallway":
                    chosenArrayId = R.array.sh_faults;
                    break;
            }

            ArrayAdapter<CharSequence> faultNatureAdapter = ArrayAdapter.createFromResource(this,
                    chosenArrayId, android.R.layout.simple_spinner_item);
            faultNatureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            faultNatureSpinner.setAdapter(faultNatureAdapter);

            faultNatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    showBLS();
                    levelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // right here
                                    showFBtns();

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    hideFBtns();
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    hideBLS();
                }
            });

        });
    }
    
    private void hideBLS() {
        levelSpinner.setVisibility(View.GONE);
        blockSpinner.setVisibility(View.GONE);
    }

    private void showBLS() {
        levelSpinner.setVisibility(View.VISIBLE);
        blockSpinner.setVisibility(View.VISIBLE);
    }

    private void hideFBtns() {
        submitBtn.setVisibility(View.GONE);
        resetBtn.setVisibility(View.GONE);
    }

    private void showFBtns() {
        submitBtn.setVisibility(View.VISIBLE);
        resetBtn.setVisibility(View.VISIBLE);
    }
}
