package com.stevecao.assignment2;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stevecao.assignment2.model.NewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

import org.w3c.dom.Text;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LocalNewsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    SharedPreferences prefs;
    ImageView loadingIV;
    private String url;
    private boolean isGlobal;
    private Context mContext;


    public LocalNewsFragment(boolean isGlobal) {

        this.isGlobal = isGlobal;

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_localnews, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        prefs = mContext.getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);

        (new UpdateNews()).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        (new UpdateNews()).execute();
    }



    private final class UpdateNews extends AsyncTask<Void, Void, String> {
        NewsAdapter newsAdapter;

        @Override
        protected String doInBackground(Void... voids) {

            if (isGlobal) {

                url = prefs.getString("com.stevecao.assignment2.globalnewsurl", "https://newsapi.org/v2/top-headlines?q=c" +
                        "oronavirus&language=en&apiKey=98d25766996b4d85a81df8c048cffe35");
                Log.d("globalurl", url);
            } else {
                url = prefs.getString("com.stevecao.assignment2.localnewsurl", "https://newsapi.org/v2/top-headlines?q=c" +
                        "oronavirus&language=en&country=sg&apiKey=98d25766996b4d85a81df8c048cffe35");
                Log.d("localurl", url);
            }
            NewsHandler newsHandler = new NewsHandler(url);
            Log.d("news", newsHandler.toString());
            newsAdapter = new NewsAdapter(mContext, newsHandler.getNewsArrayList());
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            mainRecyclerView = getView().findViewById(R.id.mainRecyclerView);
            loadingIV = getView().findViewById(R.id.loadingIV);

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
            mainRecyclerView.setAdapter(newsAdapter);
            loadingIV.setVisibility(View.GONE);
            mainRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}

