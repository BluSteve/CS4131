package com.stevecao.assignment2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stevecao.assignment2.model.CustomNews;
import com.stevecao.assignment2.model.CustomNewsAdapter;
import com.stevecao.assignment2.model.NewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class CustomNewsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ImageView loadingIV;
    TextView textView;
    private Context mContext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_localnews, container, false);
        return root;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        textView = getView().findViewById(R.id.localNewsNoUser);

        mainRecyclerView = getView().findViewById(R.id.mainRecyclerView);
        loadingIV = getView().findViewById(R.id.loadingIV);

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
        CustomNewsAdapter customNewsAdapter;
        ArrayList<CustomNews> customNewsArrayList = new ArrayList<>(0);
        boolean isDone = false;

        @Override
        protected String doInBackground(Void... voids) {

            return "Executed";


        }

        @Override
        protected void onPreExecute() {
            Log.d("customnews2", "true");


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
            db.collection("customNews")
                    .get()
                    .addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                customNewsArrayList.add(new CustomNews(
                                        document.get("headline").toString(),
                                        document.get("imageUrl").toString(),
                                        (Timestamp) document.get("timeAdded")));

                                Log.d("customNews", customNewsArrayList.toString());
                            }
                            isDone = true;

                            customNewsAdapter = new CustomNewsAdapter(mContext, customNewsArrayList);

                            Log.d("customnews3", customNewsArrayList.toString());
                            mainRecyclerView.setAdapter(customNewsAdapter);
                            loadingIV.setVisibility(View.GONE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }
}

