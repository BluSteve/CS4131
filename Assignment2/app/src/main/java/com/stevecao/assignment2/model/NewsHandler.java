package com.stevecao.assignment2.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NewsHandler {
    URL apiURL;
    public NewsHandler(String rawAPIURL) {
        try {
            this.apiURL = new URL(rawAPIURL);

            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode!=200) throw new RuntimeException("HttpResponseCode: " +responseCode);
            else {
                Scanner s = new Scanner(apiURL.openStream());
                String inline = s.nextLine();
                s.close();
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonTree = jsonParser.parse(inline);
                JsonObject jsonObject = jsonTree.getAsJsonObject();
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
