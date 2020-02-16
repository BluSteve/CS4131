package com.example.lab42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinearLayout loginLinear;
    LinearLayout adminLinear;
    EditText candNameText;
    EditText candDescText;
    Button adminBtn;
    Button guestBtn;
    ImageView NUSHLogo;
    Toolbar toolbar;
    TableLayout tableLayout;
    TextView votingPrompt;
    Button candBtn;
    Button backBtn;
    ScrollView sv;
    ArrayList<String[]> cands = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginLinear = findViewById(R.id.loginLinear);
        adminBtn = findViewById(R.id.adminBtn);
        guestBtn = findViewById(R.id.guestBtn);
        NUSHLogo = findViewById(R.id.NUSHLogo);
        toolbar = findViewById(R.id.toolbar);
        tableLayout = findViewById(R.id.tableLayout);
        votingPrompt = findViewById(R.id.votingPrompt);
        adminLinear = findViewById(R.id.adminLinear);
        candNameText = findViewById(R.id.candNameText);
        candDescText = findViewById(R.id.candDescText);
        candBtn = findViewById(R.id.candBtn);
        backBtn = findViewById(R.id.backBtn);
        sv = findViewById(R.id.sv);
        setSupportActionBar(toolbar);

        hideGuestMenu();
        hideAdminMenu();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("candidates")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cands.add(new String[]{document.getString("name"), document.getString("desc")});
                                Log.d("firestore", document.getId() + " => " + document.getData());
                            }
                            Log.d("firestore_test", cands.toString());

                            for (String[] cand : cands) {
                                TableRow row = new TableRow(MainActivity.this);
                                TableRow.LayoutParams lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);

                                row.setLayoutParams(lp);
                                Button voteBtn = new Button(MainActivity.this);
                                voteBtn.setText(R.string.voteBtnText);
                                TextView name = new TextView(MainActivity.this);
                                name.setText(cand[0]);
                                TextView desc = new TextView(MainActivity.this);
                                name.setPadding(0, 0, 30, 0);
                                desc.setText(cand[1]);
                                desc.setHeight(250);
                                desc.setWidth(500);

                                db.collection("votes").get().addOnCompleteListener((task2) -> {
                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                            Log.d("firestore", "task2 " + document2.getString("name") + " dd " + cand[0]);
                                            if (document2.getString("name").equals(cand[0].toLowerCase())) {
                                                voteBtn.setOnClickListener((s) -> {
                                                    Log.d("firestoreabc", "asdf");
                                                    db.collection("votes")
                                                            .document(cand[0].toLowerCase())
                                                            .update(
                                                                    "votes", document2.getLong("votes").intValue() + 1);
                                                });
                                            }
                                        }
                                    }
                                });


                                row.addView(voteBtn);
                                row.addView(name);
                                row.addView(desc);
                                tableLayout.addView(row);
                            }
                        } else {
                            Log.d("firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });

        adminBtn.setOnClickListener((s) -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        guestBtn.setOnClickListener((s) -> {
            hideStartMenu();
            showGuestMenu();
        });
        backBtn.setOnClickListener((s) -> {
            hideGuestMenu();
            showStartMenu();
        });
        candBtn.setOnClickListener((s) -> {
           String candName = candNameText.getText().toString();
           String candDesc = candDescText.getText().toString();
           if (!candName.equals("") && !candDesc.equals("")
           && !candName.contains(" ")) {
               Log.d("candidate", "here");
               Map<String, Object> docData = new HashMap<>();
               docData.put("name", candName);
               docData.put("desc", candDesc);
               Map<String, Object> docData2 = new HashMap<>();
               docData2.put("name", candName.toLowerCase());
               docData2.put("votes", 0);
               db.collection("candidates")
                       .add(docData);
               db.collection("votes")
                       .document(candName.toLowerCase())
                       .set(docData2);
               Toast.makeText(MainActivity.this, "Candidate added successfully", Toast.LENGTH_SHORT).show();
           }
            candDescText.setText("");
            candNameText.setText("");
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                View view = getLayoutInflater().inflate(R.layout.activity_about, adminLinear, false);
                PopupWindow popupWindow = new PopupWindow(view,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(adminLinear, Gravity.CENTER, 0, 0);
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                hideGuestMenu();
                hideAdminMenu();
                showStartMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Boolean isAdmin = intent.getBooleanExtra("admin",false);
        if (isAdmin) {
            hideStartMenu();
            hideGuestMenu();
            showAdminMenu();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
        hideGuestMenu();
        hideAdminMenu();
        showStartMenu();
    }

    private void hideStartMenu() {
        loginLinear.setVisibility(View.GONE);
        NUSHLogo.setVisibility(View.GONE);
    }

    private void hideGuestMenu() {
        sv.setVisibility(View.GONE);
        votingPrompt.setVisibility(View.GONE);
        backBtn.setVisibility(View.GONE);
    }

    private void hideAdminMenu() {
        adminLinear.setVisibility(View.GONE);
    }

    private void showStartMenu() {
        loginLinear.setVisibility(View.VISIBLE);
        NUSHLogo.setVisibility(View.VISIBLE);
    }

    private void showGuestMenu() {
        sv.setVisibility(View.VISIBLE);
        votingPrompt.setText(R.string.votingPrompt);
        votingPrompt.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
    }

    private void showAdminMenu() {
        adminLinear.setVisibility(View.VISIBLE);
        votingPrompt.setVisibility(View.VISIBLE);
        votingPrompt.setText(R.string.addCandidate);
    }
}
