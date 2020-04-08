package com.stevecao.avportal.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private static HashMap<String, String> userEvents = new HashMap<>(0);
    RecyclerView mainRecyclerView;
    ImageView loadingIV;
    TextView textView;
    Context mContext;
    boolean isAdmin;
    SharedPreferences prefs;
    SwipeRefreshLayout srl;

    public static HashMap<String, String> getUserEvents() {
        return userEvents;
    }

    public static void setUserEvents(HashMap<String, String> userEvents) {
        EventsFragment.userEvents = userEvents;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = view.getContext();
        textView = getView().findViewById(R.id.eventsNoUser);
        mainRecyclerView = getView().findViewById(R.id.eventsRV);
        loadingIV = getView().findViewById(R.id.eventsLoadingIV);
        srl = getView().findViewById(R.id.eventsSrl);
    }

    @Override
    public void onResume() {
        super.onResume();
        srl.setOnRefreshListener(() -> (new EventsFragment.UpdateEvents()).execute());
        prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
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
            db.collection("users")
                    .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            HashMap<String, String> queriedEvents = (HashMap<String, String>)
                                    task.getResult().getDocuments().get(0).get("events");
                            if (queriedEvents != null)
                                userEvents = queriedEvents;
                        }
                        db.collection("events")
                                .get()
                                .addOnSuccessListener((task2) -> {
                                    try {
                                        for (DocumentSnapshot document : task2.getDocuments()) {
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
                                                    teacherIcMap.get("email"));

                                            ArrayList<String> equiNeededMapKeys = new ArrayList<>(equiNeededMap.keySet());
                                            for (int x = 0; x < equiNeededMap.size(); x++) {
                                                String key = equiNeededMapKeys.get(x);
                                                equiNeeded.put(new Equipment(key
                                                                .split("\\|")[0],
                                                                key.split("\\|")[1]),
                                                        (long) equiNeededMap.get(key));
                                            }
                                            for (String s : (ArrayList<String>) document.get("imageUrls")) {
                                                imageUrls.add(new URL(s));
                                            }
                                            for (Timestamp t : (ArrayList<Timestamp>) document.get("dates")) {
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
//                                        if (isAdmin) {
//                                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//                                                @Override
//                                                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                                                    return false;
//                                                }
//
//                                                @Override
//                                                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                                                    builder.setTitle(mContext.getString(R.string.deleteConfirm));
//                                                    builder.setPositiveButton(mContext.getString(R.string.yes), (dialog, which) -> {
//                                                        eventAdapter.deleteItem(viewHolder.getAdapterPosition());
//                                                    });
//                                                    builder.setNegativeButton(mContext.getString(R.string.no), (dialog, which) -> {
//                                                        (new EventsFragment.UpdateEvents()).execute();
//                                                    });
//                                                    builder.show();
//                                                }
//
//                                                @Override
//                                                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                                                    super.onChildDraw(c, recyclerView, viewHolder, dX,
//                                                            dY, actionState, isCurrentlyActive);
//                                                    View itemView = viewHolder.itemView;
//                                                    int backgroundCornerOffset = 100;
//                                                    Drawable icon = mContext.getDrawable(R.drawable.ic_delete_forever_black_24dp);
//                                                    ColorDrawable background = new ColorDrawable(mContext.getColor(R.color.colorSecondary));
//
//                                                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//                                                    int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//                                                    int iconBottom = iconTop + icon.getIntrinsicHeight();
//                                                    if (dX < 0) { // Swiping to the left
//                                                        int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
//                                                        int iconRight = itemView.getRight() - iconMargin;
//                                                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//
//                                                        background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
//                                                                itemView.getTop() + 12, itemView.getRight(), itemView.getBottom() - 12);
//                                                    } else { // view is unSwiped
//                                                        background.setBounds(0, 0, 0, 0);
//                                                    }
//
//                                                    background.draw(c);
//                                                    icon.draw(c);
//                                                }
//                                            });
//                                            itemTouchHelper.attachToRecyclerView(mainRecyclerView);
//                                        }
                                        mainRecyclerView.setAdapter(eventAdapter);
                                        loadingIV.setVisibility(View.GONE);
                                        mainRecyclerView.setVisibility(View.VISIBLE);
                                        srl.setRefreshing(false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                    });

        }
    }
}
