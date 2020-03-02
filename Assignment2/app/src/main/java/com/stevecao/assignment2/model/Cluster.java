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

public class Cluster {
    private String name, address, placeId, documentId;
    private int cases;
    private GeoPoint location;

    public Cluster(String name, int cases, GeoPoint location, String documentId) {
        this.name = name;
        this.cases = cases;
        this.location = location;
        this.documentId = documentId;
    }

    public Cluster(String name, int cases, String documentId) {
        this.documentId = documentId;
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
                if (!jsonObject.get("status").getAsString().equals("ZERO_RESULTS")) {
                    JsonObject results = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();
                    JsonObject location = results.get("geometry").getAsJsonObject()
                            .get("location").getAsJsonObject();
                    geoPoint = new GeoPoint(location.get("lat").getAsDouble(),
                            location.get("lng").getAsDouble());
                    address = results.get("formatted_address").getAsString();
                    placeId = results.get("place_id").getAsString();

                    this.name = name;
                    this.cases = cases;
                    this.location = geoPoint;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GeoPoint getCoords(String locationStr) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String urlStart = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        String urlEnd = "&key=AIzaSyDcQTt51O7RKUAWSaWIs0x4uG_iTvl4pJU";
        String formattedName = locationStr.replace(' ', '+') + "+Singapore";
        String urlFull = urlStart + formattedName + urlEnd;
        URL url = null;
        GeoPoint gp;
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
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(inline);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
                if (!jsonObject.get("status").getAsString().equals("ZERO_RESULTS")) {
                    JsonObject results = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();
                    JsonObject location = results.get("geometry").getAsJsonObject()
                            .get("location").getAsJsonObject();
                    gp = new GeoPoint(location.get("lat").getAsDouble(),
                            location.get("lng").getAsDouble());
                    return gp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "name='" + name + '\'' +
                ", documentId='" + documentId + '\'' +
                ", cases=" + cases +
                ", location=" + location +
                '}';
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
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
