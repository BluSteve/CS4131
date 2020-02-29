package com.stevecao.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TemperatureFragment extends Fragment {
    FirebaseFirestore db;
    ArrayList<String> temps = new ArrayList<>(0);
    FloatingActionButton fab;
    TextView tempNoUser;
    ListView lv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_temperature, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fab = getView().findViewById(R.id.addTemp);
        lv = getView().findViewById(R.id.temperatureListView);
        tempNoUser = getView().findViewById(R.id.tempNoUser);

        fab.setOnClickListener((s) -> {
            Intent intent = new Intent(getActivity(), AddTempActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            lv.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            tempNoUser.setVisibility(View.VISIBLE);
        } else {
            lv.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            tempNoUser.setVisibility(View.GONE);
            updateLV();
        }
    }

    private void updateLV() {
        temps = new ArrayList<>(0);
        db = FirebaseFirestore.getInstance();
        db.collection("temperatures")
                .orderBy("timeTaken", Query.Direction.DESCENDING)
                .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        Log.d("temp", task.getResult().size() + "");
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd MMMM, yyyy" +
                                " - ");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("temp", FirebaseAuth.getInstance().getCurrentUser().getEmail() + "");
                            Date date = ((Timestamp) document.get("timeTaken")).toDate();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
                            temps.add(formatter.format(calendar.getTime()) + document.get("temp").toString() + "°C");

                        }
                        if (getContext() != null) {
                            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_list_item_1, temps);
                            lv.setEnabled(false);
                            lv.setOnItemClickListener(null);
                            lv.setAdapter(itemsAdapter);
                        }

                    }
                });
    }

}
