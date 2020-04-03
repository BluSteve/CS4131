package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stevecao.avportal.R;
import com.stevecao.avportal.model.Equipment;
import com.stevecao.avportal.model.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.function.LongBinaryOperator;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {
    private Context mContext;
    private ViewGroup container;
    private ArrayList<Event> events;
    private Event event;

    public EventAdapter(Context mContext, ArrayList<Event> events) {
        this.mContext = mContext;
        this.events = events;
    }

    @Override
    public EventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.container = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHolder holder, int position) {
        event = events.get(position);
        holder.titleTV.setText(event.getName());
        holder.sourceTV.setText(event.getTeacherIc().getName());
        holder.dateTV.setText(event.getStringActualDate());
        Glide.with(mContext).load(event.getMainImageUrl()).into(holder.imageView);

        holder.cardView.setOnClickListener((s) -> {
            View eventPopup = LayoutInflater.from(mContext)
                    .inflate(R.layout.event_popup, container, false);
            TextView ppTitleTV, ppTeacherIcTV, ppManpowerTV, ppContentTV;
            ListView ppReheLV, ppEquiLV;

            ViewPager ppVP = eventPopup.findViewById(R.id.eventPpVP);
            ppTitleTV = eventPopup.findViewById(R.id.eventPpTitleTV);
            ppTeacherIcTV = eventPopup.findViewById(R.id.eventPpTeacherIcTV);
            ppManpowerTV = eventPopup.findViewById(R.id.eventPpManpowerTV);
            ppContentTV = eventPopup.findViewById(R.id.eventPpContentTV);
            ppReheLV = eventPopup.findViewById(R.id.eventPpReheLV);
            ppEquiLV = eventPopup.findViewById(R.id.eventPpEquiLV);

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
                    android.R.layout.simple_list_item_1, dateStrings));
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

            ArrayList<Equipment> equiKeys = new ArrayList<>(event.getEquiNeeded().keySet());
            ArrayList<String> equiStrings = new ArrayList<>(0);
            Log.d("events", equiKeys.toString());
            for (int y = 0; y < equiKeys.size(); y++) {
                Log.d("events", equiKeys.get(y).getBrand());
                equiStrings.add(equiKeys.get(y).getBrand() + " " + equiKeys.get(y).getName()
                        + ": " + event.getEquiNeeded().get(equiKeys.get(y)));
            }
            ppEquiLV.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1,
                    equiStrings));

            Log.d("events", event.getImageUrls().toString());
            ppVP.bringToFront();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, sourceTV, dateTV;
        public ImageView imageView;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            titleTV = view.findViewById(R.id.eventTitleTV);
            sourceTV = view.findViewById(R.id.eventSourceTV);
            dateTV = view.findViewById(R.id.eventDateTV);
            imageView = view.findViewById(R.id.eventIV);
            cardView = view.findViewById(R.id.eventCV);
        }
    }



}
