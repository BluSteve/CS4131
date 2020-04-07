package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stevecao.avportal.R;
import com.stevecao.avportal.model.Announcement;
import com.stevecao.avportal.model.Equipment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SimpleTimeZone;

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
    public void deleteItem(int position) {
        Announcement toDelete = anns.get(position);
        anns.remove(position);
        FirebaseFirestore.getInstance().collection("announcements")
                .document(toDelete.getId())
                .delete()
                .addOnSuccessListener((task) -> {
                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                });
    }
    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.MyViewHolder holder, int position) {
        Announcement ann = anns.get(position);
        holder.titleTV.setText(ann.getTitle());
        holder.sourceTV.setText(ann.getAuthor());
        holder.dateTV.setText(ann.getStringDate());
        if (!ann.getImageUrl().toString().equals(""))
            Glide.with(mContext).load(ann.getImageUrl()).into(holder.imageView);
        else holder.imageView.setVisibility(View.GONE);

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
                if (!ann.getUrl().toString().equals("")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ann.getUrl().toString()));
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "No link attached", Toast.LENGTH_SHORT).show();

                }
            });
            ppSourceTV.setText(ann.getAuthor());
            ppDateTV.setText(ann.getStringDate());
            ppContentTV.setText(ann.getContent());
            if (!ann.getImageUrl().toString().equals("")){
                Glide.with(mContext).load(ann.getImageUrl()).into(ppImageView);
                ppImageView.setOnLongClickListener((s3) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.shareImage));
                    builder.setPositiveButton(mContext.getString(R.string.shareImage),
                            (dialog, which) -> {
                                Glide.with(mContext).
                                        asBitmap()
                                        .load(ann.getImageUrl())
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource,
                                                                        @Nullable Transition<? super Bitmap> transition) {
                                                Intent sendIntent = new Intent();
                                                sendIntent.setAction(Intent.ACTION_SEND);
                                                sendIntent.putExtra(Intent.EXTRA_STREAM,
                                                        getLocalBitmapUri(resource));
                                                sendIntent.setType("image/*");

                                                Intent shareIntent = Intent.createChooser(sendIntent,
                                                        mContext.getString(R.string.shareImage));
                                                mContext.startActivity(shareIntent);
                                            }
                                        });


                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
                });
            } else {
                ppImageView.setVisibility(View.GONE);
            }

            PopupWindow popup = new PopupWindow(newsPopup,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    true);
            popup.showAtLocation(holder.cardView, Gravity.CENTER, 0, 0);

        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.parse(file.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public int getItemCount() {
        return anns.size();
    }

    public ArrayList<Announcement> getAnns() {
        return anns;
    }

    public void setAnns(ArrayList<Announcement> anns) {
        this.anns = anns;
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
}
