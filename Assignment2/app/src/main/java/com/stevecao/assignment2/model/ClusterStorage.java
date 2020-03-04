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
            if (Math.abs((c.getGeoPoint().getLatitude() - geoPoint.getLatitude()))  < 0.00001 &&
                    Math.abs((c.getGeoPoint().getLongitude() - geoPoint.getLongitude())) < 0.00001) {
                return c;
            }
        }
        return null;
    }
}

