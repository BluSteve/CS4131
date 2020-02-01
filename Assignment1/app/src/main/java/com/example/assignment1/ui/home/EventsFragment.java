package com.example.assignment1.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.example.assignment1.CardInitializer;
import com.example.assignment1.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class EventsFragment extends Fragment {

    LinearLayout sv;
    ArrayList<String> descs = new ArrayList<String>(0);
    ArrayList<Integer> images = new ArrayList<Integer>(0);
    ArrayList<String> details = new ArrayList<String>(0);
    ArrayList<Integer[]> sources = new ArrayList<Integer[]>(0);
    int globalx = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        sv = root.findViewById(R.id.cardParent);

        descs.addAll(Arrays.asList(getResources().getString(R.string.cny_description),
                getResources().getString(R.string.ori_description)));
        images.addAll(Arrays.asList(R.drawable.cny1, R.drawable.ori1));
        details.addAll(Arrays.asList(getResources().getString(R.string.cny_description_long),
                getResources().getString(R.string.ori_description_long)));
        sources.addAll(Arrays.asList(new Integer[]{R.drawable.cny1, R.drawable.cny2, R.drawable.cny3},
                new Integer[]{R.drawable.ori1, R.drawable.ori2, R.drawable.ori3}));

        for (int x = 0; x < descs.size(); x++) {
            int y = x;
            View cv = inflater.inflate(R.layout.cardview, container, false);
            CardInitializer.makeCard(cv, descs.get(x), images.get(x));

            cv.findViewById(R.id.cardLinearLayout).setOnClickListener((s) -> {
                View cd = inflater.inflate(R.layout.carddetail, container, false);

                cd = CardInitializer.makeDetail(cd, descs.get(y), sources.get(y), details.get(y));

                PopupWindow popupWindow = new PopupWindow(cd,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(root, Gravity.CENTER, 0, 0);
                    });

            sv.addView(cv);
        }


        return root;
    }
}