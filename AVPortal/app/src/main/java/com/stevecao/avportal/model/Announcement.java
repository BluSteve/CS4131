package com.stevecao.avportal.model;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Announcement {
    private String title, authorName, authorEmail, content, id;
    private URL url, imageUrl;
    private Date datePublished;

    public Announcement(String authorName, String authorEmail, String title, String content,
                        URL url, URL imageUrl, Date datePublished, String id) {
        this.title = title;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
        this.url = url;
        this.imageUrl = imageUrl;
        this.datePublished = datePublished;
        this.id = id;
    }

    public String getStringDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(datePublished);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return authorName + " (" + authorEmail + ")";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "title='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", content='" + content + '\'' +
                ", url=" + url +
                ", imageUrl=" + imageUrl +
                ", datePublished=" + datePublished +
                '}';
    }
}
