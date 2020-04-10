package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.stevecao.avportal.R;
import com.stevecao.avportal.model.Announcement;
import com.stevecao.avportal.model.LightingCue;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LightsAdapter extends RecyclerView.Adapter<LightsAdapter.MyViewHolder> {
    private Context mContext;
    private ViewGroup container;
    private ArrayList<Integer> faders = new ArrayList<>(0);
    private ArrayList<Integer> currents = new ArrayList<>(0);
    private TextView cuesTV, indicator;
    private LightingCue lc;

    public LightsAdapter(Context mContext, TextView cues, TextView indicator) {
        this.mContext = mContext;
        this.cuesTV = cues;
        this.indicator = indicator;
        SharedPreferences prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        int size = prefs.getInt("com.stevecao.avportal.faderCount", 5);
        for (int x = 1; x < (size + 1); x++) faders.add(x);
        for (int x = 1; x < (size + 1); x++) currents.add(0);

        lc = new LightingCue(faders, currents);
        cuesTV.append(lc.formattedLightingCue() + "\n");
        Log.d("here", "here");
    }

    @Override
    public LightsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.container = parent;
        Log.d("here", "here");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.seekbar, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LightsAdapter.MyViewHolder holder, int position) {
        int faderNo = faders.get(position);
        holder.count.setText(faderNo + "");
        Log.d("here", faders.toString());
        refresh(holder.seekBar, position);
    }

    private void refresh(VerticalSeekBar seekBar, int position) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            long start;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currents.set(position, progress);
                indicator.setText(progress + "");
                Log.d("currents", currents.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                start = System.nanoTime();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (lc.getFaderNo() == position + 1) {
                    if (Math.abs(seekBar.getProgress() - lc.getDestination()) < 5) {
                        long end = System.nanoTime();
                        long temp = (end - start) / 1000000;
                        int errorMargin = 700;

                        Log.d("currents", "stopped " + lc.getTimeTaken() + " " + temp);
                        if (Math.abs(temp - lc.getTimeTaken()) < errorMargin) {
                            Toast toast = Toast.makeText(mContext, "Correct!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else if (temp - lc.getTimeTaken() > errorMargin) {
                            Toast toast = Toast.makeText(mContext, Math.abs(temp - lc.getTimeTaken()) + "ms too slow! ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        } else if (temp - lc.getTimeTaken() < -errorMargin) {
                            Toast toast = Toast.makeText(mContext, Math.abs(temp - lc.getTimeTaken()) + "ms too fast! ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(mContext, "Incorrect!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                    lc = new LightingCue(faders, currents);
                    cuesTV.append(lc.formattedLightingCue() + "\n");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("here2", faders.toString());
        return faders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public VerticalSeekBar seekBar;
        public TextView count;

        public MyViewHolder(View view) {
            super(view);
            seekBar = view.findViewById(R.id.seekBar);
            count = view.findViewById(R.id.seekTV);
        }
    }
}
