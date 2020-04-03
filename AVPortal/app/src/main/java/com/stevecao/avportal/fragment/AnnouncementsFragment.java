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
import com.stevecao.avportal.adapter.AnnouncementAdapter;
import com.stevecao.avportal.model.Announcement;

import org.apache.commons.lang3.text.WordUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class AnnouncementsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ImageView loadingIV;
    TextView textView;
    Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("ann", "created");
        View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mContext = view.getContext();
        textView = getView().findViewById(R.id.announcementsNoUser);
        mainRecyclerView = getView().findViewById(R.id.announcementsRV);
        loadingIV = getView().findViewById(R.id.annLoadingIV);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainRecyclerView.setVisibility(View.GONE);
            loadingIV.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        else {
            (new UpdateNews()).execute();
        }
    }

    private final class UpdateNews extends AsyncTask<Void, Void, String> {
        AnnouncementAdapter annAdapter;
        ArrayList<Announcement> anns = new ArrayList<>(0);
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
            db.collection("announcements")
                    .get()
                    .addOnSuccessListener((task) -> {
                        try {
                            for (DocumentSnapshot document : task.getDocuments()) {
                                String authorName, authorEmail, title, content;
                                URL url, imageUrl;
                                Date datePublished = ((Timestamp) document.get("datePublished"))
                                        .toDate();
                                authorName = document.get("authorName").toString();
                                authorEmail = document.get("authorEmail").toString();
                                title = WordUtils.capitalize(document.get("title").toString());
                                content = document.get("content").toString();
                                url = new URL(document.get("url").toString());
                                imageUrl = new URL(document.get("imageUrl").toString());

                                Announcement ann = new Announcement(authorName, authorEmail, title,
                                        content, url, imageUrl, datePublished);
                                anns.add(ann);
                            }

                            annAdapter = new AnnouncementAdapter(mContext, anns);

                            mainRecyclerView.setAdapter(annAdapter);
                            loadingIV.setVisibility(View.GONE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
