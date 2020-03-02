package com.stevecao.assignment2.model;

import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ClusterHandler {

    public static Cluster getCluster(String name, int cases) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlEnd = "&key=AIzaSyDcQTt51O7RKUAWSaWIs0x4uG_iTvl4pJU";
        String formattedName = name.replace(' ', '+') + "+Singapore";
        String urlFull = urlStart + formattedName + urlEnd;
        URL url = null;
        GeoPoint geoPoint;
        String address, placeId;
        try {
            url = new URL(urlFull);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            else {
                Scanner s = new Scanner(url.openStream());
                String inline = "";
                while (s.hasNext()) inline += s.nextLine();
                Log.d("cluster", inline);
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(inline);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                JsonObject results = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();
                JsonObject location = results.get("geometry").getAsJsonObject()
                        .get("location").getAsJsonObject();
                geoPoint = new GeoPoint(location.get("lat").getAsDouble(),
                        location.get("lng").getAsDouble());
                address = results.get("formatted_address").getAsString();
                placeId = results.get("place_id").getAsString();

                Cluster cluster = new Cluster(name, cases, geoPoint);
                return cluster;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
