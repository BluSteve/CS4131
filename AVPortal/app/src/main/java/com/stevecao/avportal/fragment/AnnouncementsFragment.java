package com.stevecao.avportal.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.stevecao.avportal.MainActivity;
import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.AnnouncementAdapter;
import com.stevecao.avportal.model.Announcement;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class AnnouncementsFragment extends Fragment {
    RecyclerView mainRecyclerView;
    SwipeRefreshLayout srl;
    ImageView loadingIV;
    TextView textView;
    Context mContext;
    FloatingActionButton fab;
    boolean isAdmin;
    SharedPreferences prefs;
    InputStream is;
    Button addImage;

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
        srl = getView().findViewById(R.id.annSrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null)
                Toast.makeText(mContext, "Pick a valid image!", Toast.LENGTH_SHORT).show();
            else {
                try {
                    is = mContext.getContentResolver().openInputStream(data.getData());
                    Toast.makeText(mContext, "Image chosen", Toast.LENGTH_SHORT).show();
                    addImage.setEnabled(false);
                    addImage.setText(getString(R.string.imageUploaded));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        srl.setOnRefreshListener(() -> (new UpdateNews()).execute());
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainRecyclerView.setVisibility(View.GONE);
            loadingIV.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
            isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
            Log.d("asdmin", String.valueOf(isAdmin));
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
                    addImage = customLayout.findViewById(R.id.annInputImgBtn);
                    addImage.setOnClickListener((s1) -> {
                        final int PICK_IMAGE = 1;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                    });
                    builder.setPositiveButton(mContext.getString(R.string.addBtn), (dialog, which) -> {
//                        Log.d("bitmap", is.toString());
                        String eTitle = titleET.getText().toString();
                        String eContent = contentET.getText().toString();
                        String eUrl = urlET.getText().toString();
                        if (!eUrl.startsWith("https://") && !eUrl.startsWith("http://") && !eUrl.equals(""))
                            eUrl = "https://" + eUrl;
                        if (eTitle.equals("") || !(((new UrlValidator()).isValid(eUrl)) || eUrl.equals("")))
                            Toast.makeText(mContext, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                        else {
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            String finalEUrl = eUrl;
                            String name = MainActivity.getUser().getName();
                            if (is == null) {
                                HashMap<String, Object> hashMap = new HashMap<>(0);
                                hashMap.put("authorEmail", email);
                                hashMap.put("authorName", name);
                                hashMap.put("content", eContent);
                                hashMap.put("url", finalEUrl);
                                hashMap.put("title", eTitle);
                                hashMap.put("datePublished", Timestamp.now());
                                hashMap.put("imageUrl", "");

                                FirebaseFirestore.getInstance().collection("announcements")
                                        .add(hashMap)
                                        .addOnSuccessListener((task2) -> {
                                            Log.d("storage", "success1");
                                            Toast.makeText(mContext, "Announcement made!", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                String filename = email + "_" + (new Date()).getTime();
                                StorageReference ref = FirebaseStorage.getInstance().getReference().child(filename);
                                ref.putStream(is).addOnSuccessListener((task) -> {
                                    Log.d("storage", "success");
                                    HashMap<String, Object> hashMap = new HashMap<>(0);
                                    hashMap.put("authorEmail", email);
                                    hashMap.put("authorName", name);
                                    hashMap.put("content", eContent);
                                    hashMap.put("url", finalEUrl);
                                    hashMap.put("title", eTitle);
                                    hashMap.put("datePublished", Timestamp.now());
                                    ref.getDownloadUrl().addOnSuccessListener((task3) -> {
                                        hashMap.put("imageUrl", task3.toString());
                                        FirebaseFirestore.getInstance().collection("announcements")
                                                .add(hashMap)
                                                .addOnSuccessListener((task2) -> {
                                                    Log.d("storage", "success1");
                                                    Toast.makeText(mContext, "Announcement made!", Toast.LENGTH_SHORT).show();
                                                });
                                    });
                                });
                            }
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
                                if (document.get("url").toString().equals(""))
                                    url = null;
                                else
                                    url = new URL(document.get("url").toString());
                                if (document.get("imageUrl").toString().equals(""))
                                    imageUrl = null;
                                else
                                    imageUrl = new URL(document.get("imageUrl").toString());

                                Announcement ann = new Announcement(authorName, authorEmail, title,
                                        content, url, imageUrl, datePublished, document.getId());
                                anns.add(ann);
                            }
                            Collections.sort(anns, (a2, a1) -> a1.getDatePublished().compareTo(a2.getDatePublished()));
                            annAdapter = new AnnouncementAdapter(mContext, anns);
                            if (isAdmin) {
                                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                                    @Override
                                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                        return false;
                                    }

                                    @Override
                                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(mContext.getString(R.string.deleteConfirm));
                                        builder.setPositiveButton(mContext.getString(R.string.yes), (dialog, which) -> {
                                            annAdapter.deleteItem(viewHolder.getAdapterPosition());
                                        });
                                        builder.setNegativeButton(mContext.getString(R.string.no), (dialog, which) -> {
                                            (new UpdateNews()).execute();
                                        });
                                        builder.show();
                                    }

                                    @Override
                                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                                        super.onChildDraw(c, recyclerView, viewHolder, dX,
                                                dY, actionState, isCurrentlyActive);
                                        View itemView = viewHolder.itemView;
                                        int backgroundCornerOffset = 100;
                                        Drawable icon = mContext.getDrawable(R.drawable.ic_delete_forever_black_24dp);
                                        ColorDrawable background = new ColorDrawable(mContext.getColor(R.color.colorSecondary));

                                        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                                        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                                        int iconBottom = iconTop + icon.getIntrinsicHeight();
                                        if (dX < 0) { // Swiping to the left
                                            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                                            int iconRight = itemView.getRight() - iconMargin;
                                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                                            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                                                    itemView.getTop() + 12, itemView.getRight(), itemView.getBottom() - 12);
                                        } else { // view is unSwiped
                                            background.setBounds(0, 0, 0, 0);
                                        }

                                        background.draw(c);
                                        icon.draw(c);
                                    }
                                });
                                itemTouchHelper.attachToRecyclerView(mainRecyclerView);
                            }
                            mainRecyclerView.setAdapter(annAdapter);
                            loadingIV.setVisibility(View.GONE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                            srl.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
