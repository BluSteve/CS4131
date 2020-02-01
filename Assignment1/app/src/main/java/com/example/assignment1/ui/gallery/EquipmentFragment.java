package com.example.assignment1.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment1.CardInitializer;
import com.example.assignment1.R;

import java.util.ArrayList;
import java.util.Arrays;

public class EquipmentFragment extends Fragment {

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

        descs.addAll(Arrays.asList(getResources().getString(R.string.cy),
                getResources().getString(R.string.steve),
                getResources().getString(R.string.wes)));
        images.addAll(Arrays.asList(R.drawable.cy, R.drawable.steve, R.drawable.wes));
        details.addAll(Arrays.asList(getResources().getString(R.string.cy_description),
                getResources().getString(R.string.steve_description),
                getResources().getString(R.string.wes_description)));
        sources.addAll(Arrays.asList(new Integer[]{R.drawable.cy},
                new Integer[]{R.drawable.steve}, new Integer[]{R.drawable.wes, R.drawable.wes2}));

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