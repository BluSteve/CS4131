package com.stevecao.avportal.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.EventAdapter;
import com.stevecao.avportal.model.Equipment;
import com.stevecao.avportal.model.Event;
import com.stevecao.avportal.model.User;


import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EventsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ImageView loadingIV;
    TextView textView;
    Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("ann", "created");
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = view.getContext();
        textView = getView().findViewById(R.id.eventsNoUser);
        mainRecyclerView = getView().findViewById(R.id.eventsRV);
        loadingIV = getView().findViewById(R.id.eventsLoadingIV);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainRecyclerView.setVisibility(View.GONE);
            loadingIV.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            (new UpdateEvents()).execute();
        }
    }

    private final class UpdateEvents extends AsyncTask<Void, Void, String> {
        EventAdapter eventAdapter;
        ArrayList<Event> events = new ArrayList<>(0);

        @Override
        protected String doInBackground(Void... voids) {
            return "Executed";
        }

        @Override
        protected void onPreExecute() {

            mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
            Glide.with(getContext())
                    .load(R.drawable.loading2)
                    .into(loadingIV);
            loadingIV.setVisibility(View.VISIBLE);
            mainRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("events")
                    .get()
                    .addOnSuccessListener((task) -> {
                        try {
                            for (DocumentSnapshot document : task.getDocuments()) {
                                Log.d("doc", document.toString());
                                User teacherIc;
                                HashMap<Equipment, Long> equiNeeded = new HashMap<>(0);
                                String name, desc, location, id;
                                ArrayList<URL> imageUrls = new ArrayList<>(0);
                                HashMap<String, String> teacherIcMap = (HashMap<String, String>) document.get("teacherIc");
                                HashMap<String, Object> equiNeededMap = (HashMap<String, Object>) document.get("equiNeeded");
                                long manpower;
                                ArrayList<Date> dates = new ArrayList<>(0);
                                teacherIc = new User(teacherIcMap.get("name"), teacherIcMap.get("number"),
                                        teacherIcMap.get("email"), "teacherIc");

                                ArrayList<String> equiNeededMapKeys = new ArrayList<>(equiNeededMap.keySet());
                                for (int x = 0; x < equiNeededMap.size(); x++) {
                                    String key = equiNeededMapKeys.get(x);
                                    equiNeeded.put(new Equipment(key
                                            .split("\\|")[0],
                                            key.split("\\|")[1]),
                                            (long) equiNeededMap.get(key));
                                }
                                for (String s: (ArrayList<String>) document.get("imageUrls")) {
                                    imageUrls.add(new URL(s));
                                }
                                for (Timestamp t: (ArrayList<Timestamp>) document.get("dates")) {
                                    dates.add(t.toDate());
                                }
                                name = document.get("name").toString();
                                desc = document.get("desc").toString();
                                location = document.get("location").toString();
                                id = document.getId();
                                manpower = (long) document.get("manpower");

                                Event event = new Event(teacherIc, name, desc, location, id,
                                        imageUrls, dates, equiNeeded, manpower);
                                events.add(event);
                            }

                            eventAdapter = new EventAdapter(mContext, events);
                            Log.d("events", events.toString());
                            mainRecyclerView.setAdapter(eventAdapter);
                            loadingIV.setVisibility(View.GONE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
