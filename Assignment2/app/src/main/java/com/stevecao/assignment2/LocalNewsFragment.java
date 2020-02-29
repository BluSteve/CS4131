package com.stevecao.assignment2;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stevecao.assignment2.model.NewsAdapter;
import com.stevecao.assignment2.model.NewsHandler;

public class LocalNewsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    private String url;
    private Context mContext;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_localnews, container, false);
        return root;
    }
    public LocalNewsFragment(String url) {
        this.url = url;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mContext = view.getContext();
        mainRecyclerView = getView().findViewById(R.id.mainRecyclerView);
        NewsHandler newsHandler = new NewsHandler(url);
        Log.d("news", newsHandler.toString());
        NewsAdapter newsAdapter = new NewsAdapter(view.getContext(), newsHandler.getNewsArrayList());
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mainRecyclerView.setAdapter(newsAdapter);
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        mainRecyclerView = getView().findViewById(R.id.mainRecyclerView);
//        NewsHandler newsHandler = new NewsHandler(url);
//        Log.d("news", newsHandler.toString());
//        NewsAdapter newsAdapter = new NewsAdapter(mContext, newsHandler.getNewsArrayList());
//        mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mainRecyclerView.setAdapter(newsAdapter);
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d("localnews", "paused");
//    }
}

