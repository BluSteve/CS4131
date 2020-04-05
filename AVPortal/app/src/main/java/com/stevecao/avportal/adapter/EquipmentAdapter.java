package com.stevecao.avportal.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.provider.CalendarContract;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.stevecao.avportal.R;
import com.stevecao.avportal.model.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.MyViewHolder> {
    private Context mContext;
    private ViewGroup container;
    private ArrayList<Equipment> equis;
    private SharedPreferences prefs;
    private boolean isAdmin;

    public EquipmentAdapter(Context mContext, ArrayList<Equipment> equis) {
        this.mContext = mContext;
        this.equis = equis;
    }

    @Override
    public EquipmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.container = parent;
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.equi_card, parent, false);
        prefs = mContext.getSharedPreferences("com.stevecao.avportal", Context.MODE_PRIVATE);
        isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
        return new MyViewHolder(itemView);
    }

    public void deleteItem(int position) {
        Equipment toDelete = equis.get(position);
        equis.remove(position);
        FirebaseFirestore.getInstance().collection("equipment")
                .document(toDelete.getId())
                .delete()
                .addOnSuccessListener((task) -> {
                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                    notifyItemRemoved(position);
                });
    }
    @Override
    public void onBindViewHolder(@NonNull EquipmentAdapter.MyViewHolder holder, int position) {
        Equipment equi = equis.get(position);
        holder.nameTV.setText(equi.getFullName());
        holder.nameTV.setSelected(true);
        holder.quantityTV.setText(mContext.getString(R.string.quantity) + " " + equi.getQuantity());
        holder.costTV.setText("$" + equi.getCost());
        holder.editBtn.bringToFront();
        Glide.with(mContext).load(equi.getMainImageUrl()).into(holder.imageView);
        boolean darkMode = prefs.getBoolean("com.stevecao.avportal.darkMode", true);
        boolean isAdmin = prefs.getBoolean("com.stevecao.avportal.isAdmin", false);
        if (darkMode) ImageViewCompat.setImageTintList(holder.editBtn,
                ColorStateList.valueOf(mContext.getColor(R.color.white)));
        else ImageViewCompat.setImageTintList(holder.editBtn,
                ColorStateList.valueOf(mContext.getColor(R.color.black)));
        if (isAdmin) holder.editBtn.setVisibility(View.VISIBLE);


        holder.editBtn.setOnClickListener((s) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.editEquiQuantity));

            final View customLayout = LayoutInflater.from(mContext).inflate(R.layout.input, null);
            builder.setView(customLayout);
            EditText input = customLayout.findViewById(R.id.inputEditText);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String enteredQuan = input.getText().toString();
                try {
                    if (enteredQuan.equals(""))
                        Toast.makeText(mContext, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                    else{
                        FirebaseFirestore.getInstance().collection("equipment")
                                .document(equi.getId())
                                .update("quantity", Long.parseLong(enteredQuan))
                                .addOnSuccessListener((task) -> {
                                    Toast.makeText(mContext, "Update successful!", Toast.LENGTH_SHORT).show();
                                });
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, "Please enter valid values!", Toast.LENGTH_SHORT).show();
                }

            });

            builder.show();
        });
        holder.cardView.setOnClickListener((s) -> {
            View equiPopup = LayoutInflater.from(mContext)
                    .inflate(R.layout.equi_popup, container, false);
            TextView ppNameTV, ppQuantityTV, ppCostTV, ppContentTV;

            ViewPager ppVP = equiPopup.findViewById(R.id.equiPpVP);
            ppNameTV = equiPopup.findViewById(R.id.equiPpNameTV);
            ppQuantityTV = equiPopup.findViewById(R.id.equiPpQuantityTV);
            ppCostTV = equiPopup.findViewById(R.id.equiPpCostTV);
            ppContentTV = equiPopup.findViewById(R.id.equiPpContentTV);

            ppVP.setAdapter(new ImageAdapter(mContext, equi.getImageUrls()));
            ppNameTV.setText(equi.getFullName());
            ppQuantityTV.setText(mContext.getString(R.string.quantity) + " " + equi.getQuantity());
            ppCostTV.setText("$" + equi.getCost());
            ppContentTV.setText(equi.getDesc());

            PopupWindow popup = new PopupWindow(equiPopup,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    true);
            popup.showAtLocation(holder.cardView, Gravity.CENTER, 0, 0);
        });
    }

    @Override
    public int getItemCount() {
        return equis.size();
    }

    public void clear() {
        equis.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Equipment> list) {
        equis.addAll(list);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTV, quantityTV, costTV;
        public ImageButton editBtn;
        public ImageView imageView;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            nameTV = view.findViewById(R.id.equiNameTV);
            quantityTV = view.findViewById(R.id.equiQuantityTV);
            costTV = view.findViewById(R.id.equiCostTV);
            imageView = view.findViewById(R.id.equiIV);
            cardView = view.findViewById(R.id.equiCV);
            editBtn = view.findViewById(R.id.equiEditBtn);
        }
    }


}
