package com.stevecao.avportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.seismic.ShakeDetector;
import com.stevecao.avportal.model.StageAction;
import com.stevecao.avportal.model.StageActionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class StageModeActivity extends AppCompatActivity {
    TextToSpeech t1;
    ArrayList<Button> btns = new ArrayList<>(0);
    HashMap<Button, StageAction> hashMap = new HashMap<>(0);
    Button edit, add, delete, macro;
    FrameLayout housing;
    TextView notifsTV, noUser;
    boolean isEditing, isMacroing;
    float dX, dY;
    SensorManager sm;
    ShakeDetector sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stagemode);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        add = findViewById(R.id.stageAddBtn);
        edit = findViewById(R.id.stageEditBtn);
        delete = findViewById(R.id.stageDeleteBtn);
        macro = findViewById(R.id.stageMacroBtn);
        housing = findViewById(R.id.btnHouse);
        notifsTV = findViewById(R.id.notifsTV);
        noUser = findViewById(R.id.stageNoUser);
//        File file = new File(this.getExternalFilesDir(null),
//                "stageActions.txt");
//        try {
//            PrintWriter writer = new PrintWriter(file);
//            writer.write("");
//            writer.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            add.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            macro.setVisibility(View.GONE);
            housing.setVisibility(View.GONE);
            notifsTV.setVisibility(View.GONE);
            noUser.setVisibility(View.VISIBLE);
        } else {
            t1 = new TextToSpeech(this, status -> {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);

                    FirebaseFirestore.getInstance().collection("stageactions")
                            .document("cue").addSnapshotListener((snapshot, e) -> {
                        if (e != null) {
                            Toast.makeText(this, "An error has occurred.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("sizes", btns.size() + " " + hashMap.size());
                            notifsTV.append("\n" + snapshot.get("notif").toString());
                            shakeItBaby();
                            t1.speak(snapshot.get("tts").toString(), TextToSpeech.QUEUE_FLUSH, null);
                        }
                    });
                }
            });


            StageActionHandler.updateActionsFromCache(this);
            for (StageAction action : StageActionHandler.getStageActions()) {
                Log.d("actions", action.toString());
                Button b = new Button(this);
                b.setText(action.getTitle());
                b.setOnClickListener((s) -> {
                    Log.d("hae", "here");
                    HashMap<String, Object> toUpload = new HashMap<>(0);
                    toUpload.put("notif", action.getNotif());
                    toUpload.put("tts", action.getTtstext());
                    toUpload.put("rand", (new Random()).nextInt(1000));
                    FirebaseFirestore.getInstance().collection("stageactions")
                            .document("cue")
                            .set(toUpload)
                            .addOnSuccessListener((task) -> {
                                Toast.makeText(this, "Instruction sent!", Toast.LENGTH_SHORT).show();
                            });
                });
                b.setX(action.getX());
                b.setY(action.getY());
                b.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300));
                hashMap.put(b, action);
                btns.add(b);
                housing.addView(b);
            }

            macro.setOnClickListener((s) -> {
                if (!isMacroing) {
                    macro.setBackgroundColor(getColor(R.color.colorSecondary));
                    isMacroing = true;
                    Log.d("btn", btns.toString());
                    for (Button btn : btns) {
                        btn.setOnClickListener((s1) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(getString(R.string.macroConfirm));
                            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                                for (Button btn2 : btns) {
                                    btn2.setOnClickListener(new MyClickListener());
                                }
                                sd = new ShakeDetector(() -> {
                                    Log.d("shook", "shook");
                                    btn.performClick();
                                });
                                sd.start(sm);
                                macro.setBackgroundColor(getColor(R.color.colorPrimary));
                                macro.setEnabled(false);
                                Log.d("sensi", prefs.getInt("com.stevecao.avportal.shakeIntensity",
                                        ShakeDetector.SENSITIVITY_MEDIUM) + "");
                                sd.setSensitivity(prefs.getInt("com.stevecao.avportal.shakeIntensity",
                                        ShakeDetector.SENSITIVITY_MEDIUM));
                            });
                            builder.show();
                        });
                    }
                } else {
                    isMacroing = false;
                }
            });
            add.setOnClickListener((s) -> {
                Log.d("stage", "here");
                Button b = new Button(this);
                b.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 300));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.newStageAction));
                final View customLayout = LayoutInflater.from(this).inflate(R.layout.stage_input, null);
                builder.setView(customLayout);
                EditText eTitleTV, eNotifTV, eTtsTV;
                eTitleTV = customLayout.findViewById(R.id.stageInputTitle);
                eNotifTV = customLayout.findViewById(R.id.stageInputNotif);
                eTtsTV = customLayout.findViewById(R.id.stageInputTTS);
                builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    String eTitle = eTitleTV.getText().toString();
                    String eNotif = eNotifTV.getText().toString();
                    String eTts = eTtsTV.getText().toString();
                    if (eTitle.equals("") || eNotif.equals("")) {
                        Toast.makeText(this, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                    } else {
                        String buttonId = (new Date()).getTime() + (new Random()).nextInt(1000) + "";
                        StageAction sa = new StageAction(buttonId, eTitle, eNotif, eTts, 0, 0);
                        hashMap.put(b, sa);
                        StageActionHandler.addAction(sa);
                        b.setText(eTitle);
                        btns.add(b);
                        housing.addView(b);
                    }
                });
                builder.show();
            });


            edit.setOnClickListener((s) -> {
                if (!isEditing) {
                    edit.setText(getString(R.string.finish));
                    isEditing = true;
                    for (Button btn : btns) {
                        btn.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent event) {
                                switch (event.getAction()) {

                                    case MotionEvent.ACTION_DOWN:

                                        dX = view.getX() - event.getRawX();
                                        dY = view.getY() - event.getRawY();
                                        break;

                                    case MotionEvent.ACTION_MOVE:

                                        view.animate()
                                                .x(event.getRawX() + dX)
                                                .y(event.getRawY() + dY)
                                                .setDuration(0)
                                                .start();
                                        break;
                                    default:
                                        return false;
                                }
                                Log.d("ontouch", view.getX() + "");
                                return true;
                            }
                        });
                    }
                } else {
                    edit.setText(getString(R.string.edit));
                    isEditing = false;
                    Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show();
                    for (Button btn : btns) {
                        btn.setOnTouchListener(null);
                        btn.setOnClickListener(new MyClickListener());
                        StageActionHandler.updateXYofAction(hashMap.get(btn).getId(), btn.getX(), btn.getY());
                    }
                }
            });
            delete.setOnClickListener((s) -> {
                Log.d("btn", btns.toString());
                for (Button btn : btns) {
                    btn.setOnClickListener((s1) -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(getString(R.string.deleteConfirm));
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            btn.setVisibility(View.GONE);
                            btns.remove(btn);
                            for (Button btn2 : btns) {
                                btn2.setOnClickListener(new MyClickListener());
                            }
                            StageActionHandler.deleteAction(hashMap.get(btn).getId());
                            hashMap.remove(btn);
                        });
                        builder.show();
                    });
                }
            });
        }
    }

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(300);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sd != null)
            sd.start(sm);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sd != null)
            sd.stop();
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("sizes", "clicked");
            StageAction action = hashMap.get(v);
            HashMap<String, Object> toUpload = new HashMap<>(0);
            toUpload.put("notif", action.getNotif());
            toUpload.put("tts", action.getTtstext());
            toUpload.put("rand", (new Random()).nextInt(1000));
            FirebaseFirestore.getInstance().collection("stageactions")
                    .document("cue")
                    .set(toUpload)
                    .addOnSuccessListener((task) -> {
                        Toast.makeText(StageModeActivity.this, "Instruction sent!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
