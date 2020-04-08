package com.stevecao.avportal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.stevecao.avportal.MainActivity;
import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.AnnouncementAdapter;
import com.stevecao.avportal.adapter.LightsAdapter;
import com.stevecao.avportal.model.Announcement;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LightsFragment extends Fragment {
    TextView lightingCuesTV;
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
        seekBarRV = view.findViewById(R.id.seekBarRV);
        mContext = view.getContext();
    }


    @Override
    public void onResume() {
        super.onResume();
        LightsAdapter lightsAdapter = new LightsAdapter(mContext, lightingCuesTV);
        seekBarRV.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false));
        seekBarRV.setItemAnimator(new DefaultItemAnimator());
        seekBarRV.setAdapter(lightsAdapter);
    }

}
