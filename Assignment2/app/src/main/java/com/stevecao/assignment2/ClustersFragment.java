package com.stevecao.assignment2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.stevecao.assignment2.model.Cluster;
import com.stevecao.assignment2.model.ClusterStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClustersFragment extends Fragment implements OnMapReadyCallback{
    Context mContext;
    FirebaseFirestore db;
    ImageView clustersLoadingIV;
    FloatingActionButton fab;
    SupportMapFragment mapFragment;
    ArrayList<Cluster> clusters = new ArrayList<>(0);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_clusters, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.mContext = view.getContext();
        clustersLoadingIV = getView().findViewById(R.id.clustersLoadingIV);
        fab = getView().findViewById(R.id.addCluster);


        fab.setOnClickListener((s) -> {
            Intent intent = new Intent(getActivity(), AddClusterActivity.class);
            startActivity(intent);
        });
        Glide.with(getContext()).load(R.drawable.loading2).into(clustersLoadingIV);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clustersLoadingIV.setVisibility(View.VISIBLE);
        clustersLoadingIV.bringToFront();
    }

    private void updateMaps(GoogleMap googleMap) {
        LatLng sg = new LatLng(1.294306, 103.816316);
        ClusterStorage.setClusters(clusters);
        Log.d("clusterstorage", ClusterStorage.getClusters().toString());
        for (Cluster cluster : clusters) {
            LatLng latLng = new LatLng(cluster.getLocation().getLatitude(),
                    cluster.getLocation().getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(cluster.getName())
                    .snippet(cluster.getCases() + " cases"));
        }
        SharedPreferences prefs = getView().getContext().getSharedPreferences("com.stevecao.assignment2", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isAdmin", false))
            fab.setVisibility(View.VISIBLE);
        else
            fab.setVisibility(View.GONE);
        clustersLoadingIV.setVisibility(View.GONE);
        googleMap.setOnInfoWindowClickListener((marker) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Share cluster");
            builder.setPositiveButton(mContext.getString(R.string.shareBtn),
                    (dialog, which) -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, marker.getTitle());
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        mContext.startActivity(shareIntent);

                    });
            AlertDialog alertDialog = builder.create();
            Log.d("marker", "true");
            alertDialog.show();
        });
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sg, 10));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        db = FirebaseFirestore.getInstance();
        db.collection("clusters").get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int cases = Integer.parseInt(document.get("cases").toString());
                            String name = document.get("name").toString();
                            if (document.getGeoPoint("location") != null) {
                                GeoPoint location = document.getGeoPoint("location");
                                clusters.add(new Cluster(name, cases, location, document.getId()));

                            } else {
                                Cluster temp = new Cluster(name, cases, document.getId());
                                clusters.add(temp);
                                Map<String, Object> entry = new HashMap<>();
                                entry.put("location", temp.getLocation());
                                db.collection("clusters")
                                        .document(document.getId())
                                        .set(entry, SetOptions.merge())
                                        .addOnSuccessListener((editTask) -> {
                                            Log.d("edittask", document.getId());
                                        });
                            }

                        }

                        updateMaps(googleMap);
                    }
                });

    }

}
