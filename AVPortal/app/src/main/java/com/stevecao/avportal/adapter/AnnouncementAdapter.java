package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
    private ViewGroup container;
    private ArrayList<Announcement> anns;

    public AnnouncementAdapter(Context mContext, ArrayList<Announcement> anns) {
        this.mContext = mContext;
        this.anns = anns;
    }

    @Override
    public AnnouncementAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.container = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ann_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.MyViewHolder holder, int position) {
        Announcement ann = anns.get(position);
        holder.titleTV.setText(ann.getTitle());
        holder.sourceTV.setText(ann.getAuthor());
        holder.dateTV.setText(ann.getStringDate());
        Glide.with(mContext).load(ann.getImageUrl()).into(holder.imageView);

        holder.cardView.setOnClickListener((s) -> {
            View newsPopup = LayoutInflater.from(mContext)
                    .inflate(R.layout.ann_popup, container, false);
            TextView ppTitleTV, ppSourceTV, ppDateTV, ppContentTV;
            ImageView ppImageView;
            ppTitleTV = newsPopup.findViewById(R.id.ppTitleTV);
            ppSourceTV = newsPopup.findViewById(R.id.ppSourceTV);
            ppDateTV = newsPopup.findViewById(R.id.ppDateTV);
            ppContentTV = newsPopup.findViewById(R.id.ppContentTV);
            ppImageView = newsPopup.findViewById(R.id.ppImageView);

            SpannableString annTitle = new SpannableString(ann.getTitle());
            annTitle.setSpan(new UnderlineSpan(), 0, annTitle.length(), 0);
            ppTitleTV.setText(annTitle);
            ppTitleTV.setMovementMethod(LinkMovementMethod.getInstance());
            ppTitleTV.setOnClickListener((s2) -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ann.getUrl().toString()));
                mContext.startActivity(intent);
            });
            ppSourceTV.setText(ann.getAuthor());
            ppDateTV.setText(ann.getStringDate());
            ppContentTV.setText(ann.getContent());
            Glide.with(mContext).load(ann.getImageUrl()).into(ppImageView);
            ppImageView.setOnLongClickListener((s3) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Share Post");
                builder.setPositiveButton(mContext.getString(R.string.shareBtn),
                        (dialog, which) -> {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(ann.getImageUrl().toString()));
                            sendIntent.setType("image/jpg");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            mContext.startActivity(shareIntent);

                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            });

            PopupWindow popup = new PopupWindow(newsPopup,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    true);
            popup.showAtLocation(holder.cardView, Gravity.CENTER, 0, 0);

        });
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
