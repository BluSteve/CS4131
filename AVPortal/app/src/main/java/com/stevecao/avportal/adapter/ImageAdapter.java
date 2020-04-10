package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.stevecao.avportal.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<URL> imageUrls;
    LayoutInflater inflater;

    public ImageAdapter(Context mContext, ArrayList<URL> imageUrls) {
        this.mContext = mContext;
        this.imageUrls = imageUrls;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.image_slider, view, false);
        assert imageLayout != null;
        final ImageView imageView = imageLayout
                .findViewById(R.id.sliderIV);
        Glide.with(mContext).asBitmap().load(imageUrls.get(position)).into(imageView);
        imageView.setOnLongClickListener((s3) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.shareImage));
            builder.setPositiveButton(mContext.getString(R.string.shareImage),
                    (dialog, which) -> {
                        Glide.with(mContext).
                                asBitmap()
                                .load(imageUrls.get(position))
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable Transition<? super Bitmap> transition) {
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_STREAM,
                                                getLocalBitmapUri(resource));
                                        sendIntent.setType("image/*");

                                        Intent shareIntent = Intent.createChooser(sendIntent,
                                                mContext.getString(R.string.shareImage));
                                        mContext.startActivity(shareIntent);
                                    }
                                });


                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
        view.addView(imageLayout);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.parse(file.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
