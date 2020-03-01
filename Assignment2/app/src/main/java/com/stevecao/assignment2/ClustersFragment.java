package com.stevecao.assignment2;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClustersFragment extends Fragment implements OnMapReadyCallback {
    Context mContext;
    FirebaseFirestore db;
    ImageView clustersLoadingIV;
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
        Glide.with(getContext()).load(R.drawable.loading2).into(clustersLoadingIV);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        clustersLoadingIV.setVisibility(View.VISIBLE);
        clustersLoadingIV.bringToFront();
    }

    private void updateMap() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        db = FirebaseFirestore.getInstance();
        db.collection("clusters").get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        clustersLoadingIV.setVisibility(View.GONE);
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int cases = Integer.parseInt(document.get("cases").toString());
                            String name = document.get("name").toString();
                            GeoPoint location = document.getGeoPoint("location");
                            clusters.add(new Cluster(name, cases, location));
                        }
                        LatLng sg = new LatLng(1.294306, 103.816316);
                        for (Cluster cluster: clusters) {
                            LatLng latLng = new LatLng(cluster.getLocation().getLatitude(),
                                    cluster.getLocation().getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(latLng)
                                    .title(cluster.getName() + " - " + cluster.getCases() + " Cases"));
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sg,10));
                    }
                });

    }

    private class Cluster {
        private String name;
        private int cases;
        private GeoPoint location;

        public Cluster(String name, int cases, GeoPoint location) {
            this.name = name;
            this.cases = cases;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCases() {
            return cases;
        }

        public void setCases(int cases) {
            this.cases = cases;
        }

        public GeoPoint getLocation() {
            return location;
        }

        public void setLocation(GeoPoint location) {
            this.location = location;
        }


    }
}
