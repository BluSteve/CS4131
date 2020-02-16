package com.stevecao.lab4.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.stevecao.lab4.R;

public class AnalyticsFragment extends Fragment {

    private AnalyticsViewModel analyticsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        analyticsViewModel =
                ViewModelProviders.of(this).get(AnalyticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_analytics, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        analyticsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}