package com.stevecao.avportal.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stevecao.avportal.R;
import com.stevecao.avportal.adapter.EquipmentAdapter;
import com.stevecao.avportal.model.Equipment;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class EquipmentFragment extends Fragment {
    RecyclerView mainRecyclerView;
    SwipeRefreshLayout srl;
    ImageView loadingIV;
    TextView textView;
    EditText searchText;
    Spinner sortSpinner;
    Button searchBtn;
    Context mContext;
    EquipmentAdapter equiAdapter;
    View equiView;
    SharedPreferences prefs;
    String selectedSort = "";
    boolean isAdmin;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_equipment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        textView = getView().findViewById(R.id.equiNoUser);
        mainRecyclerView = getView().findViewById(R.id.equiRV);
        loadingIV = getView().findViewById(R.id.equiLoadingIV);
        searchBtn = getView().findViewById(R.id.equiSearchBtn);
        searchText = getView().findViewById(R.id.equiSearchText);
        sortSpinner = getView().findViewById(R.id.equiSortSpinner);
        equiView = getView().findViewById(R.id.equiView);
        srl = getView().findViewById(R.id.equiSRL);
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
        updateEqui("", "");

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSort = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSort = "";
            }
        });
        searchBtn.setOnClickListener((s) -> {
            String enteredSearch = searchText.getText().toString();
            if (enteredSearch.equals("") && selectedSort.equals(""))
                Toast.makeText(mContext, "No search term detected", Toast.LENGTH_SHORT).show();
            else updateEqui(enteredSearch, selectedSort);
        });
        srl.setOnRefreshListener(() -> {
            updateEqui("", "");
        });
    }

    private void updateEqui(String searchTerm, String sortTerm) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mainRecyclerView.setVisibility(View.GONE);
            loadingIV.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        } else {
            mainRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mainRecyclerView.setItemAnimator(new DefaultItemAnimator());
            Glide.with(getContext())
                    .load(R.drawable.loading2)
                    .into(loadingIV);
            loadingIV.setVisibility(View.VISIBLE);
            mainRecyclerView.setVisibility(View.GONE);

            ArrayList<Equipment> equis = new ArrayList<>(0);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("equipment")
                    .get()
                    .addOnSuccessListener((task) -> {
                        try {
                            for (DocumentSnapshot document : task.getDocuments()) {
                                String brand, name, desc, id;
                                ArrayList<URL> imageUrls = new ArrayList<>(0);
                                long quantity, cost;

                                for (String s : (ArrayList<String>) document.get("imageUrls")) {
                                    imageUrls.add(new URL(s));
                                }
                                brand = document.get("brand").toString();
                                name = document.get("name").toString();
                                desc = document.get("desc").toString();
                                id = document.getId();
                                quantity = (long) document.get("quantity");
                                cost = (long) document.get("cost");

                                Equipment equi = new Equipment(brand, name, desc, cost, quantity,
                                        imageUrls, id);
                                equis.add(equi);
                            }

                            boolean isSearched = true;
                            if (searchTerm.equals("") && sortTerm.equals("")) isSearched = false;
                            if (isSearched) {
                                if (!searchTerm.equals("")) {
                                    ArrayList<Equipment> temp = new ArrayList<>(0);
                                    for (Equipment e : equis) {
                                        String s = (e.getFullName()).toLowerCase();
                                        if (s.contains(searchTerm.toLowerCase()))
                                            temp.add(e);
                                    }
                                    equis.clear();
                                    equis.addAll(temp);
                                }

                                if (!sortTerm.equals("")) {
                                    final String costA = mContext.getResources().getStringArray(R.array.sorts)[0];
                                    final String costD = mContext.getResources().getStringArray(R.array.sorts)[1];
                                    final String quanA = mContext.getResources().getStringArray(R.array.sorts)[2];
                                    final String quanD = mContext.getResources().getStringArray(R.array.sorts)[3];
                                    if (selectedSort.equals(costA)) {
                                        Collections.sort(equis, (o1, o2) -> (int) (o1.getCost() - o2.getCost()));
                                    } else if (selectedSort.equals(costD)) {
                                        Collections.sort(equis, (o1, o2) -> (int) (o2.getCost() - o1.getCost()));
                                    } else if (selectedSort.equals(quanA)) {
                                        Collections.sort(equis, (o1, o2) -> (int) (o1.getQuantity() - o2.getQuantity()));
                                    } else if (selectedSort.equals(quanD)) {
                                        Collections.sort(equis, (o1, o2) -> (int) (o2.getQuantity() - o1.getQuantity()));
                                    }
                                }
                            }

                            equiAdapter = new EquipmentAdapter(mContext, equis);
                            Log.d("events", equis.toString());
                            mainRecyclerView.setAdapter(equiAdapter);
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
                                            equiAdapter.deleteItem(viewHolder.getAdapterPosition());
                                        });
                                        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> {
                                            updateEqui(searchTerm, sortTerm);
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
                            loadingIV.setVisibility(View.GONE);
                            equiView.setVisibility(View.VISIBLE);
                            mainRecyclerView.setVisibility(View.VISIBLE);
                            srl.setRefreshing(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });


        }
    }
}
