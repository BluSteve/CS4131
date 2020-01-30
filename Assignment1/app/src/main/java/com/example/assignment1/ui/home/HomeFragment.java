package com.example.assignment1.ui.home;

import android.os.Bundle;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.assignment1.CardInitializer;
import com.example.assignment1.R;

public class HomeFragment extends Fragment {

    LinearLayout sv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_events, container, false);
        sv = root.findViewById(R.id.cardParent);
        sv.addView((new CardInitializer()).generateCard(getResources().getDrawable(R.drawable.av), "AV IS FUN"));
        return root;
    }
}