package com.stevecao.assignment2.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stevecao.assignment2.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<News> newsArrayList;

    public NewsAdapter(Context mContext, ArrayList<News> newsArrayList) {
        this.mContext = mContext;
        this.newsArrayList = newsArrayList;
        Log.d("newsarray", "" + this.newsArrayList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        News news = newsArrayList.get(position);
        holder.titleTV.setText(news.getTitle());
        holder.sourceTV.setText(news.getSource());
        holder.dateTV.setText(news.getDatePublishedString());
        Glide.with(mContext).load(news.getImageURL()).into(holder.imageView);

        holder.cardView.setOnClickListener((s) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl().toString()));
            mContext.startActivity(intent);
        });
        holder.cardView.setOnLongClickListener((s) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Share Post");
            builder.setPositiveButton(mContext.getString(R.string.shareBtn),
                    (dialog, which) -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, news.getUrl().toString());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        mContext.startActivity(shareIntent);
                        
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
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
