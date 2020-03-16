package com.stevecao.assignment2.model;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.firestore.GeoPoint;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ClinicHandler {
    private static ArrayList<Clinic> clinics = new ArrayList<>(0);
    private static URL apiUrl;

    static {
        try {
            apiUrl = new URL("https://www.flugowhere.gov.sg/data.json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Clinic> getClinics() {
        return clinics;
    }

    public static void setClinics(ArrayList<Clinic> clinics) {
        ClinicHandler.clinics = clinics;
    }

    public static boolean updateClinicsFromCache(Context context) {
        clinics.clear();
        File file = new File(context.getExternalFilesDir(null),
                "clinicsCache.txt");
        if (!file.exists())
            return false;
        try {
            Scanner s = new Scanner(file);
            String inline = s.nextLine();

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonTree = jsonParser.parse(inline);
            JsonArray jsonArray = jsonTree.getAsJsonArray();

            for (JsonElement je : jsonArray) {
                JsonObject rawClinic = je.getAsJsonObject();
                String content = "";
                try {
                    Clinic toAdd = new Clinic(
                            rawClinic.get("clinicName").getAsString(),
                            rawClinic.get("address").getAsString(),
                            rawClinic.get("clinicTelephoneNo").getAsString(),
                            new GeoPoint(rawClinic.get("lat").getAsDouble(),
                                    rawClinic.get("long").getAsDouble()),
                            rawClinic.get("type").getAsString());
                    clinics.add(toAdd);
                } catch (Exception e) {
                    continue;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean updateClinics(Context context) {
        clinics.clear();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != 200)
                return false;
            else {
                Scanner s = new Scanner(apiUrl.openStream());
                String inline = "";
                while (s.hasNext()) {
                    inline += s.nextLine();
                }
                s.close();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(inline);
                JsonArray jsonArray = jsonTree.getAsJsonArray();

                for (JsonElement je : jsonArray) {
                    JsonObject rawClinic = je.getAsJsonObject();
                    String content = "";
                    try {
                        Clinic toAdd = new Clinic(
                                rawClinic.get("clinicName").getAsString(),
                                rawClinic.get("address").getAsString(),
                                rawClinic.get("clinicTelephoneNo").getAsString(),
                                new GeoPoint(rawClinic.get("lat").getAsDouble(),
                                        rawClinic.get("long").getAsDouble()),
                                rawClinic.get("type").getAsString());
                        clinics.add(toAdd);
                    } catch (Exception e) {
                        continue;
                    }

                }


                File file = new File(context.getExternalFilesDir(null),
                        "clinicsCache.txt");
                file.createNewFile();
                FileWriter out = new FileWriter(file);
//                Log.d("jsonarray", jsonArray.toString());
                out.write(jsonArray.toString());
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("clinics", clinics.toString());
        return true;
    }

}
