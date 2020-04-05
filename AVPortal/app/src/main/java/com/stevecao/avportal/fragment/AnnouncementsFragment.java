package com.stevecao.avportal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.AnnouncementAdapter;
import com.stevecao.avportal.model.Announcement;

import org.apache.commons.lang3.text.WordUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class AnnouncementsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    ImageView loadingIV;
    TextView textView;
    Context mContext;
    FloatingActionButton fab;
    boolean isAdmin;
    SharedPreferences prefs;
    Bitmap bmp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("ann", "created");
        View root = inflater.inflate(R.layout.fragment_announcements, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        textView = getView().findViewById(R.id.announcementsNoUser);
        mainRecyclerView = getView().findViewById(R.id.announcementsRV);
        loadingIV = getView().findViewById(R.id.annLoadingIV);
        fab = getView().findViewById(R.id.annFab);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null)
                Toast.makeText(mContext, "Pick a valid image!", Toast.LENGTH_SHORT).show();
            else {
                try {
                    InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
                    bmp = BitmapFactory.decodeStream(inputStream);
                    Toast.makeText(mContext, "Image chosen", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainRecyclerView.setVisibility(View.GONE);
            loadingIV.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        else {
            prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
            isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
            if (isAdmin) {
                fab.setVisibility(View.VISIBLE);
                fab.bringToFront();

                fab.setOnClickListener((s) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.newAnn));

                    final View customLayout = LayoutInflater.from(mContext).inflate(R.layout.ann_input, null);
                    builder.setView(customLayout);
                    EditText titleET, contentET, urlET;
                    titleET = customLayout.findViewById(R.id.annInputTitle);
                    contentET = customLayout.findViewById(R.id.annInputContent);
                    urlET = customLayout.findViewById(R.id.annInputUrl);
                    Button addImage = customLayout.findViewById(R.id.annInputImgBtn);
                    addImage.setOnClickListener((s1) -> {
                        final int PICK_IMAGE = 1;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    });
                    builder.setPositiveButton(mContext.getString(R.string.addBtn), (dialog, which) -> {
                        Log.d("bitmap", bmp.toString());
                        // TODO upload bitmap
                        String eTitle = titleET.getText().toString();
                        String eContent =  contentET.getText().toString();
                        String eUrl = urlET.getText().toString();
                        if (eTitle.equals(""))
                            Toast.makeText(mContext, "Please entered valid title!", Toast.LENGTH_SHORT).show();
                        else {

                        }
                    });



                    builder.show();
                });
            }
            (new UpdateNews()).execute();
        }
    }

    private final class UpdateNews extends AsyncTask<Void, Void, String> {
        AnnouncementAdapter annAdapter;
        ArrayList<Announcement> anns = new ArrayList<>(0);
        @Override
        protected String doInBackground(Void... voids) {
            return "Executed";
        }

        @Override
        protected void onPreExecute() {
            mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
            Glide.with(getContext())
                    .load(R.drawable.loading2)
                    .into(loadingIV);
            loadingIV.setVisibility(View.VISIBLE);
            mainRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("announcements")
                    .get()
                    .addOnSuccessListener((task) -> {
                        try {
                            for (DocumentSnapshot document : task.getDocuments()) {
                                String authorName, authorEmail, title, content;
                                URL url, imageUrl;
                                Date datePublished = ((Timestamp) document.get("datePublished"))
                                        .toDate();
                                authorName = document.get("authorName").toString();
                                authorEmail = document.get("authorEmail").toString();
                                title = WordUtils.capitalize(document.get("title").toString());
                                content = document.get("content").toString();
                                url = new URL(document.get("url").toString());
                                imageUrl = new URL(document.get("imageUrl").toString());

                                Announcement ann = new Announcement(authorName, authorEmail, title,
                                        content, url, imageUrl, datePublished);
                                anns.add(ann);
                            }

                            annAdapter = new AnnouncementAdapter(mContext, anns);

                            mainRecyclerView.setAdapter(annAdapter);
                            loadingIV.setVisibility(View.GONE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
