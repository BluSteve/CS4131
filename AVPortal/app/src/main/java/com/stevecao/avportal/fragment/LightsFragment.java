package com.stevecao.avportal.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.LightsAdapter;

public class LightsFragment extends Fragment {
    TextView lightingCuesTV, indicator;
    RecyclerView seekBarRV;
    Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lights, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lightingCuesTV = view.findViewById(R.id.lightingCuesTV);
        indicator = view.findViewById(R.id.lightsIndicator);
        seekBarRV = view.findViewById(R.id.seekBarRV);
        mContext = view.getContext();
        lightingCuesTV.setTypeface(mContext.getResources().getFont(R.font.pt_mono));
        indicator.setTextColor(mContext.getColor(R.color.white));
        indicator.setTypeface(mContext.getResources().getFont(R.font.pt_mono));
    }


    @Override
    public void onResume() {
        super.onResume();
        LightsAdapter lightsAdapter = new LightsAdapter(mContext, lightingCuesTV, indicator);
        seekBarRV.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false));
        seekBarRV.setItemAnimator(new DefaultItemAnimator());
        seekBarRV.setAdapter(lightsAdapter);
    }

}
