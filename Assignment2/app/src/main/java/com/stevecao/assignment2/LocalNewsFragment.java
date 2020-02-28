package com.stevecao.assignment2;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class LocalNewsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_localnews, container, false);
        return root;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        mainRecyclerView = getView().findViewById(R.id.mainRecyclerView);
    }
}
