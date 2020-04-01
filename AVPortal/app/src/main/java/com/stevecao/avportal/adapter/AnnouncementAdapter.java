package com.stevecao.avportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stevecao.avportal.R;
import com.stevecao.avportal.model.Announcement;

import java.util.ArrayList;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<Announcement> anns;

    public AnnouncementAdapter(Context mContext, ArrayList<Announcement> anns) {
        this.mContext = mContext;
        this.anns = anns;
    }
    @Override
    public AnnouncementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.MyViewHolder holder, int position) {
        Announcement ann = anns.get(position);
        holder.titleTV.setText(ann.getTitle());
        holder.sourceTV.setText(ann.getAuthor());
        holder.dateTV.setText(ann.getStringDate());
        Glide.with(mContext).load(ann.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return anns.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV, sourceTV, dateTV;
        public ImageView imageView;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            titleTV = view.findViewById(R.id.titleTV);
            sourceTV = view.findViewById(R.id.sourceTV);
            dateTV = view.findViewById(R.id.dateTV);
            imageView = view.findViewById(R.id.imageView);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    public ArrayList<Announcement> getAnns() {
        return anns;
    }

    public void setAnns(ArrayList<Announcement> anns) {
        this.anns = anns;
    }
}
