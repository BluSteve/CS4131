package com.example.assignment1;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class CardInitializer {
    public static View makeCard(View cv, String descText, int imageSource) {
        TextView desc = cv.findViewById(R.id.desc);
        ImageView image = cv.findViewById(R.id.image);
        desc.setText(descText);
        image.setImageResource(imageSource);
        cv.findViewById(R.id.cardLinearLayout).setOnClickListener((s) -> {
            Log.d("debug123", "clicked1");

        });
        return cv;
    }

    public static View makeDetail(View cv, String descTitle, Integer[] imageSources,
                                  String descText) {
        TextView detailTitle = cv.findViewById(R.id.detailTitle);
        TextView detailDesc = cv.findViewById(R.id.detailDesc);
        ImageView img2 = cv.findViewById(R.id.imageView2);
        ImageView img3 = cv.findViewById(R.id.imageView3);
        ImageView img4 = cv.findViewById(R.id.imageView4);
        ImageView[] list = new ImageView[] {img2, img3, img4};

        detailTitle.setText(descTitle);
        detailDesc.setText(descText);
        for (int x = 0; x<3; x++) {
            if (imageSources.length > x)
                list[x].setImageResource(imageSources[x]);
            else {
                for (int y =x; y< 3; y++ ) {
                    list[y].setVisibility(View.GONE);
                }
            }
        }
        return cv;
    }
}