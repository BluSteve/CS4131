package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.stevecao.avportal.R;
import com.stevecao.avportal.fragment.EventsFragment;
import com.stevecao.avportal.model.Announcement;
import com.stevecao.avportal.model.Equipment;
import com.stevecao.avportal.model.Event;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    private Context mContext;
    private ViewGroup container;
    Button ppSignUpBtn;
    ListView ppAdminLV;
    TextView ppApplTV;
    ImageView ppLoadingIV;
    private ArrayList<Event> events;
    private SharedPreferences prefs;
    private boolean isAdmin;

    public EventAdapter(Context mContext, ArrayList<Event> events) {
        this.mContext = mContext;
        this.events = events;
    }

    @Override
    public EventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.container = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
        return new MyViewHolder(itemView);
    }

    public void refreshAdmin(Event event) {
        ppSignUpBtn.setVisibility(View.GONE);
        ppAdminLV.setVisibility(View.VISIBLE);

        // Get list of applications from Firebase
        FirebaseFirestore.getInstance().collection("users")
                .get()
                .addOnSuccessListener((task) -> {
                    ArrayList<String> appsString = new ArrayList<>(0);
                    ArrayList<String> idAppsString = new ArrayList<>(0);
                    for (DocumentSnapshot d : task.getDocuments()) {
                        if (d.contains("events")) {
                            HashMap<String, String> queriedEvents = (HashMap<String,
                                    String>) d.get("events");
                            ArrayList<String> partookEvents = new ArrayList(queriedEvents
                                    .keySet());
                            for (String eventId : partookEvents) {
                                if (eventId.equals(event.getId())) {
                                    String status = "";
                                    switch (queriedEvents.get(eventId)) {
                                        case "pending":
                                            status = mContext.getString(R.string.pending);
                                            break;
                                        case "approved":
                                            status = mContext.getString(R.string.approved);
                                            break;
                                    }
                                    appsString.add(d.get("name").toString() + " - " + status);
                                    idAppsString.add(d.getId());
                                }
                            }
                        }
                    }

                    // Admin ListView initialization
                    if (appsString.size() == 0)
                        ppApplTV.setVisibility(View.GONE);
                    else {
                        ppApplTV.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adminAdapter = new ArrayAdapter<String>(mContext,
                                android.R.layout.simple_list_item_1, appsString);
                        ppAdminLV.setAdapter(adminAdapter);
                        ppAdminLV.setOnItemClickListener((parent, view, position2, id) -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle(mContext.getString(R.string.approvalConfirm));
                            String oriStatus = appsString.get(position2).split(" - ")[1];
                            String crewId = idAppsString.get(position2);
                            if (oriStatus.equals(mContext.getString(R.string.pending))) {
                                builder.setPositiveButton(mContext.getString(R.string.approve), (dialog, which) -> {
                                    ppLoadingIV.bringToFront();
                                    ppLoadingIV.setVisibility(View.VISIBLE);
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(crewId)
                                            .get()
                                            .addOnSuccessListener((task2) -> {
                                                HashMap<String, String> hashMap = (HashMap<String, String>) task2.get("events");
                                                hashMap.put(event.getId(), "approved");
                                                HashMap<String, Object> toMerge = new HashMap<>(0);
                                                toMerge.put("events", hashMap);
                                                FirebaseFirestore.getInstance().collection("users")
                                                        .document(crewId)
                                                        .set(toMerge, SetOptions.merge())
                                                        .addOnSuccessListener((task3) -> {
                                                            ppLoadingIV.setVisibility(View.GONE);
                                                            refreshAdmin(event);
                                                            Toast.makeText(mContext, "Status changed!", Toast.LENGTH_SHORT).show();
                                                        });
                                            });

                                });
                            } else if (oriStatus.equals(mContext.getString(R.string.approved))) {
                                builder.setPositiveButton(mContext.getString(R.string.disapprove), (dialog, which) -> {
                                    ppLoadingIV.bringToFront();
                                    ppLoadingIV.setVisibility(View.VISIBLE);
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(crewId)
                                            .get()
                                            .addOnSuccessListener((task2) -> {
                                                HashMap<String, String> hashMap = (HashMap<String, String>) task2.get("events");
                                                hashMap.put(event.getId(), "pending");
                                                HashMap<String, Object> toMerge = new HashMap<>(0);
                                                toMerge.put("events", hashMap);
                                                FirebaseFirestore.getInstance().collection("users")
                                                        .document(crewId)
                                                        .set(toMerge, SetOptions.merge())
                                                        .addOnSuccessListener((task3) -> {
                                                            ppLoadingIV.setVisibility(View.GONE);
                                                            refreshAdmin(event);
                                                            Toast.makeText(mContext, "Status changed!", Toast.LENGTH_SHORT).show();
                                                        });
                                            });

                                });

                            }

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        });
                    }
                });

    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHolder holder, int position) {
        Event event = events.get(position);
        Log.d("events2", event.toString());
        holder.titleTV.setText(event.getName());
        holder.sourceTV.setText(event.getTeacherIc().getName());
        holder.dateTV.setText(event.getStringActualDate());
        holder.doubleTick.bringToFront();
        Glide.with(mContext).load(event.getMainImageUrl()).into(holder.imageView);

        if (EventsFragment.getUserEvents().containsKey(event.getId())) {
            switch (EventsFragment.getUserEvents().get(event.getId())) {
                case "pending":
                    holder.doubleTick.setVisibility(View.VISIBLE);
                    break;
                case "approved":
                    holder.doubleTick.setVisibility(View.VISIBLE);
                    holder.doubleTick.setColorFilter(mContext.getColor(R.color.colorPrimary));
                    break;
            }
        }

        holder.cardView.setOnClickListener((s) -> {
            View eventPopup = LayoutInflater.from(mContext)
                    .inflate(R.layout.event_popup, container, false);
            TextView ppTitleTV, ppTeacherIcTV, ppManpowerTV, ppContentTV;
            ListView ppReheLV, ppEquiLV;

            ppLoadingIV = eventPopup.findViewById(R.id.eventPpLoadingIV);
            ppSignUpBtn = eventPopup.findViewById(R.id.eventPpSignUpBtn);
            ViewPager ppVP = eventPopup.findViewById(R.id.eventPpVP);
            ppTitleTV = eventPopup.findViewById(R.id.eventPpTitleTV);
            ppTeacherIcTV = eventPopup.findViewById(R.id.eventPpTeacherIcTV);
            ppManpowerTV = eventPopup.findViewById(R.id.eventPpManpowerTV);
            ppContentTV = eventPopup.findViewById(R.id.eventPpContentTV);
            ppReheLV = eventPopup.findViewById(R.id.eventPpReheLV);
            ppEquiLV = eventPopup.findViewById(R.id.eventPpEquiLV);
            ppAdminLV = eventPopup.findViewById(R.id.eventPpAdminLV);
            ppApplTV = eventPopup.findViewById(R.id.eventPpApplTV);

            // Dates ListView initialization
            ArrayList<String> startDates = event.getStringStartDates();
            ArrayList<String> endDates = event.getStringEndDates();
            ArrayList<String> dateStrings = new ArrayList<>(0);
            for (int x = 0; x < startDates.size(); x++) {
                if (x == startDates.size() - 1) {
                    dateStrings.add("Actual: " + startDates.get(x) + " - " + endDates.get(x));
                } else {
                    dateStrings.add("Rehearsal " + (x + 1) + ": " + startDates.get(x)
                            + " - " + endDates.get(x));
                }
            }
            ppReheLV.setAdapter(new ArrayAdapter<>(mContext,
                    R.layout.listview_text, dateStrings));
            ppReheLV.setOnItemLongClickListener((parent, view, position2, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.addToCal));
                builder.setPositiveButton(mContext.getString(R.string.addToCal),
                        (dialog, which) -> {
                            Intent sendIntent = new Intent(Intent.ACTION_INSERT)
                                    .setData(CalendarContract.Events.CONTENT_URI)
                                    .putExtra(CalendarContract.Events.TITLE, event.getName())
                                    .putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation())
                                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                            event.getStartDates().get(position2).getTime())
                                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                            event.getEndDates().get(position2).getTime());

                            Intent shareIntent = Intent.createChooser(sendIntent,
                                    mContext.getString(R.string.addToCal));
                            mContext.startActivity(shareIntent);
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            });

            // Equipment ListView initialization
            ArrayList<Equipment> equiKeys = new ArrayList<>(event.getEquiNeeded().keySet());
            ArrayList<String> equiStrings = new ArrayList<>(0);
            for (int y = 0; y < equiKeys.size(); y++) {
                equiStrings.add(equiKeys.get(y).getFullName() + ": " +
                        event.getEquiNeeded().get(equiKeys.get(y)));
            }
            ppEquiLV.setAdapter(new ArrayAdapter<>(mContext, R.layout.listview_text,
                    equiStrings));
            ppEquiLV.setOnItemClickListener((parent, view, position2, id) -> {
                Log.d("equi", "clicked");
                ArrayList<Equipment> equis = new ArrayList<>(0);

                ppLoadingIV.setVisibility(View.VISIBLE);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("equipment")
                        .get()
                        .addOnSuccessListener((task) -> {
                            ppLoadingIV.setVisibility(View.GONE);
                            try {
                                Log.d("equi", "clicked");
                                for (DocumentSnapshot document : task.getDocuments()) {
                                    String brand, name, desc, equiId;
                                    ArrayList<URL> imageUrls = new ArrayList<>(0);
                                    long quantity, cost;

                                    if (document.get("imageUrls") != null) {
                                        for (String s2 : (ArrayList<String>) document.get("imageUrls")) {
                                            imageUrls.add(new URL(s2));
                                        }
                                    }
                                    brand = document.get("brand").toString();
                                    name = document.get("name").toString();
                                    desc = document.get("desc").toString();
                                    equiId = document.getId();
                                    quantity = (long) document.get("quantity");
                                    cost = (long) document.get("cost");

                                    Equipment equi = new Equipment(brand, name, desc, cost, quantity,
                                            imageUrls, equiId);
                                    equis.add(equi);
                                }

                                Equipment equi = new Equipment("", "");
                                for (Equipment e : equis) {
                                    if (e.getFullName().toLowerCase().equals(equiKeys.get(position2).getFullName().toLowerCase())) {
                                        equi = e;
                                    }
                                }
                                if (equi.getBrand().equals(""))
                                    Toast.makeText(mContext, "Equipment does not exist!", Toast.LENGTH_SHORT).show();
                                View equiPopup = LayoutInflater.from(mContext)
                                        .inflate(R.layout.equi_popup, container, false);
                                TextView ppNameTV, ppQuantityTV, ppCostTV, equiPpContentTV;

                                ViewPager equiPpVP = equiPopup.findViewById(R.id.equiPpVP);
                                ppNameTV = equiPopup.findViewById(R.id.equiPpNameTV);
                                ppQuantityTV = equiPopup.findViewById(R.id.equiPpQuantityTV);
                                ppCostTV = equiPopup.findViewById(R.id.equiPpCostTV);
                                equiPpContentTV = equiPopup.findViewById(R.id.equiPpContentTV);

                                equiPpVP.setAdapter(new ImageAdapter(mContext, equi.getImageUrls()));
                                ppNameTV.setText(equi.getFullName());
                                ppQuantityTV.setText(mContext.getString(R.string.quantity) + " " + equi.getQuantity());
                                ppCostTV.setText("$" + equi.getCost());
                                equiPpContentTV.setText(equi.getDesc());

                                PopupWindow popup = new PopupWindow(equiPopup,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        true);
                                popup.showAtLocation(holder.cardView, Gravity.CENTER, 0, 0);
                            } catch (Exception e) { e.printStackTrace(); }
                        });
            });

            // Admin stuff
            Log.d("admin", String.valueOf(isAdmin));
            if (isAdmin) {
                refreshAdmin(event);
            } else {
                if (EventsFragment.getUserEvents().containsKey(event.getId())) {
                    switch (EventsFragment.getUserEvents().get(event.getId())) {
                        case "pending":
                            ppSignUpBtn.setEnabled(false);
                            ppSignUpBtn.setText(mContext.getString(R.string.pending));
                            break;
                        case "approved":
                            ppSignUpBtn.setEnabled(false);
                            ppSignUpBtn.setText(mContext.getString(R.string.approved));
                            break;
                    }
                }

                ppSignUpBtn.setOnClickListener((s1) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.signUpConfirm) + " " + event.getName());
                    builder.setPositiveButton(mContext.getString(R.string.yes), (dialog, which) -> {
                        ppLoadingIV.bringToFront();
                        ppSignUpBtn.setEnabled(false);
                        ppLoadingIV.setVisibility(View.VISIBLE);
                        Log.d("eventa", String.valueOf(EventsFragment.getUserEvents() == null));
                        EventsFragment.getUserEvents().put(event.getId(), "pending");
                        HashMap<String, Object> hashMap = new HashMap<>(0);
                        hashMap.put("events", EventsFragment.getUserEvents());
                        FirebaseFirestore.getInstance().collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(hashMap, SetOptions.merge())
                                .addOnCompleteListener((task2) -> {
                                    ppLoadingIV.setVisibility(View.GONE);
                                    ppSignUpBtn.setText(mContext.getString(R.string.pending));
                                    Toast.makeText(mContext, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                });
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                });
            }

            ppVP.setAdapter(new ImageAdapter(mContext, event.getImageUrls()));
            ppTitleTV.setText(event.getName());
            ppTeacherIcTV.setText(event.getTeacherIc().getName() + " (" + event.getTeacherIc().getEmail()
                    + ") - " + event.getTeacherIc().getNumber());
            ppManpowerTV.setText("Manpower needed: " + event.getManpower());
            ppContentTV.setText(event.getDesc());

            PopupWindow popup = new PopupWindow(eventPopup,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    true);
            popup.showAtLocation(holder.cardView, Gravity.CENTER, 0, 0);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void deleteItem(int position) {
        Event toDelete = events.get(position);
        events.remove(position);
        FirebaseFirestore.getInstance().collection("events")
                .document(toDelete.getId())
                .delete()
                .addOnSuccessListener((task) -> {
                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, sourceTV, dateTV;
        public ImageView imageView, doubleTick;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            titleTV = view.findViewById(R.id.eventTitleTV);
            sourceTV = view.findViewById(R.id.eventSourceTV);
            dateTV = view.findViewById(R.id.eventDateTV);
            imageView = view.findViewById(R.id.eventIV);
            cardView = view.findViewById(R.id.eventCV);
            doubleTick = view.findViewById(R.id.doubleTick);
        }
    }


}
