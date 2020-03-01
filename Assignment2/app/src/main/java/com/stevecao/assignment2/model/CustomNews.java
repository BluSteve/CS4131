package com.stevecao.assignment2.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class CustomNews {
    String headline, imageUrl;
    Timestamp timestamp;

    public CustomNews(String headline, String imageUrl, Timestamp timestamp) {
        this.headline = headline;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CustomNews{" +
                "headline='" + headline + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDatePublishedString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(this.timestamp.toDate());
    }
}