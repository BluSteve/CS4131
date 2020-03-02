package com.stevecao.assignment2.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class ClusterStorage {
    public static ArrayList<Cluster> clusters;

    public static ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public static void setClusters(ArrayList<Cluster> clusters) {
        ClusterStorage.clusters = clusters;
    }

    public static Cluster getClusterByGeoPoint(GeoPoint geoPoint) {
        for (Cluster c: clusters) {
            if (c.getLocation().equals(geoPoint))
                return c;
        }
        return null;
    }
}

