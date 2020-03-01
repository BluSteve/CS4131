package com.stevecao.assignment2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stevecao.assignment2.model.NewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

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
    ImageView tempLoadingIV;
    Context mContext;
    boolean loggedIn;

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
        tempLoadingIV = getView().findViewById(R.id.tempLoadingIV);
        mContext = view.getContext();
        Glide.with(getContext())
                .load(R.drawable.loading2)
                .into(tempLoadingIV);
        fab.setOnClickListener((s) -> {
            Intent intent = new Intent(getActivity(), AddTempActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        (new UpdateTemps()).execute();
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
                        if (temps.size() == 0) {
                            temps.add(mContext.getString(R.string.noTemp));
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

    private final class UpdateTemps extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            if (loggedIn)
                updateLV();
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                lv.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                tempNoUser.setText(getString(R.string.noUser));
                tempNoUser.setVisibility(View.VISIBLE);
            } else {
                tempLoadingIV.setVisibility(View.VISIBLE);
                lv.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                tempNoUser.setVisibility(View.GONE);
                loggedIn = true;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            tempLoadingIV.setVisibility(View.GONE);

        }
    }

}
