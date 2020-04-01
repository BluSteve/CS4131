package com.stevecao.assignment2.model;

import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.stevecao.assignment2.MainActivity;

import org.apache.commons.lang3.text.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ClusterHandler {
    private static ArrayList<Cluster> clusters = new ArrayList<>(0);
    private static ArrayList<ArrayList<String>> clusterNames = new ArrayList<>(0);


    public static void updateClusters() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL("https://co.vid19.sg/");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            Scanner s = new Scanner(url.openStream());
            String inline = "";
            while (s.hasNext()) {
                inline += s.nextLine();
            }
            s.close();
            Log.d("inline", inline);

            Document doc = Jsoup.parse(inline);
            Elements table = doc.select("table").get(0).select("tbody");
            Log.d("inline", table.toString());
            Elements rows = table.select("tr");
            db.collection("clusters").get().addOnSuccessListener((task) -> {
                for (DocumentSnapshot document : task.getDocuments()) {
                    Log.d("jsoupdoc", document.toString());
                    clusterNames.add(new ArrayList<>(Arrays.asList(document.get("name").toString(),
                            document.getId())));
                }

                Log.d("jsoup", clusterNames.toString());
                for (Element row : rows) {
                    Elements cols = row.select("td");
                    Log.d("jsoup", cols.get(0).text() + cols.get(1).text());
                    String location = cols.get(0).text();
                    int cases = Integer.parseInt(cols.get(1).text());
                    // add new cluster
                    GeoPoint coords = Cluster.getCoords(location);
                    boolean b = false;
                    String id = "";
                    for (ArrayList<String> list : clusterNames) {
                        if (location.equals(list.get(0))) {
                            b = true;
                            id = list.get(1);
                        }
                    }
                    if (b) { // pre-existing

                        db.collection("clusters")
                                .document(id)
                                .update("cases", cases);
                    } else { // new
                        GeoPoint gp = Cluster.getCoords(location);
                        if (gp!=null) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("name", location);
                            data.put("cases", cases);
                            data.put("location", gp);
                            db.collection("clusters")
                                    .add(data);
                        }
                    }


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Cluster> getClusters() {
        return clusters;
    }

    public static void setClusters(ArrayList<Cluster> clusters) {
        ClusterHandler.clusters = clusters;
    }
}
