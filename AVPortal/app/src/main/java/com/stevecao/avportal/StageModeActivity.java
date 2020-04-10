package com.stevecao.avportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
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
    boolean isEditing, isMacroing, isDeleting;
    float dX, dY;
    SensorManager sm;
    ShakeDetector sd;
    PowerManager pm;
    PowerManager.WakeLock wl;
    int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = this.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        if (prefs.getBoolean("com.stevecao.avportal.darkMode", true))
            setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stagemode);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "avportal:stagemode");
        wl.acquire();

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
        notifsTV.setTypeface(getResources().getFont(R.font.pt_mono));
        notifsTV.setMovementMethod(new ScrollingMovementMethod());
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
                            notifsTV.append("\n\n" + snapshot.get("notif").toString());
                            shakeItBaby();
                            if (snapshot.get("tts") != null)
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
                    toUpload.put("rand", (new Random()).nextInt(100000));
                    FirebaseFirestore.getInstance().collection("stageactions")
                            .document("cue")
                            .set(toUpload)
                            .addOnSuccessListener((task) -> {
                                Toast.makeText(this, "Instruction sent!", Toast.LENGTH_SHORT).show();
                            });
                });
                b.setX(action.getX());
                b.setY(action.getY());
                b.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 400));
                b.setMinWidth(400);
                hashMap.put(b, action);
                btns.add(b);
                housing.addView(b);
            }

            macro.setOnClickListener((s) -> {
                if (!isMacroing) {
                    current = macro.getCurrentTextColor();
                    macro.setBackgroundColor(getColor(R.color.colorSecondary));
                    macro.setTextColor(getColor(R.color.black));
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
                                macro.setBackgroundColor(getColor(R.color.colorPrimaryLight));
                                macro.setTextColor(getColor(R.color.lesswhite));
                                btn.setBackgroundColor(getColor(R.color.colorSecondary));
                                btn.setTextColor(getColor(R.color.black));
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
                    macro.setBackgroundColor(getColor(R.color.colorPrimary));
                    macro.setTextColor(current);
                }
            });
            add.setOnClickListener((s) -> {
                Log.d("stage", "here");
                Button b = new Button(this);
                b.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 400));
                b.setMinWidth(400);
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
                        b.setOnClickListener(new MyClickListener());
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
                if (!isDeleting) {
                    delete.setBackgroundColor(getColor(R.color.red));
                    isDeleting = true;
                    Log.d("btn", btns.toString());
                    for (Button btn : btns) {
                        btn.setOnClickListener((s1) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle(getString(R.string.deleteConfirm));
                            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                                btn.setVisibility(View.GONE);
                                if (sd != null)
                                    sd.stop();
                                btns.remove(btn);
                                for (Button btn2 : btns) {
                                    btn2.setOnClickListener(new MyClickListener());
                                }
                                StageActionHandler.deleteAction(hashMap.get(btn).getId());
                                hashMap.remove(btn);
                                delete.setBackgroundColor(getColor(R.color.colorPrimary));
                                isDeleting = false;
                            });
                            builder.show();
                        });
                    }
                } else {
                    isDeleting = false;
                    delete.setBackgroundColor(getColor(R.color.colorPrimary));
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        t1.shutdown();
        wl.release();
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("sizes", "clicked");
            StageAction action = hashMap.get(v);
            HashMap<String, Object> toUpload = new HashMap<>(0);
            toUpload.put("notif", action.getNotif());
            toUpload.put("tts", action.getTtstext());
            toUpload.put("rand", (new Random()).nextInt(100000));
            FirebaseFirestore.getInstance().collection("stageactions")
                    .document("cue")
                    .set(toUpload)
                    .addOnSuccessListener((task) -> {
                        Toast.makeText(StageModeActivity.this, "Instruction sent!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
