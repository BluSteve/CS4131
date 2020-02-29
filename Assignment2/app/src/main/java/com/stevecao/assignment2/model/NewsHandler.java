package com.stevecao.assignment2.model;

import android.os.StrictMode;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NewsHandler {
    URL apiURL;
    ArrayList<News> newsArrayList = new ArrayList<News>(0);
    public NewsHandler(String rawAPIURL) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                JsonArray newses = jsonObject.get("articles").getAsJsonArray(); // array of articles/newses

                for (JsonElement je: newses) {
                    JsonObject rawNews = je.getAsJsonObject();
                    String content = "";
                    if (!rawNews.get("content").isJsonNull()) content = rawNews.get("content").getAsString();
                    try {
                        News news = new News(rawNews.get("title").getAsString(),
                                rawNews.get("source").getAsJsonObject().get("name").getAsString(),
                                content, rawNews.get("url").getAsString(),
                                rawNews.get("urlToImage").getAsString(),
                                rawNews.get("publishedAt").getAsString());
                        newsArrayList.add(news);
                    }
                    catch (Exception e) {
                        continue;
                    }

                }
            }
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public URL getApiURL() {
        return apiURL;
    }

    public void setApiURL(URL apiURL) {
        this.apiURL = apiURL;
    }

    @Override
    public String toString() {
        return "NewsHandler{" +
                "apiURL=" + apiURL +
                ", newsArrayList=" + newsArrayList +
                '}';
    }

    public ArrayList<News> getNewsArrayList() {
        return newsArrayList;
    }

    public void setNewsArrayList(ArrayList<News> newsArrayList) {
        this.newsArrayList = newsArrayList;
    }
}
