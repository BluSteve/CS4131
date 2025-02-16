package com.stevecao.assignment2.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class News {

    private String title, source, content;
    private URL url, imageURL;
    private Date datePublished;


    public News (String title, String source, String content,
                 String url, String imageURL,
                 String rawDatePublished) throws MalformedURLException{
        this.title = title;
        this.source = source;
        this.content = content;
        this.url = new URL(url);
        this.imageURL = new URL(imageURL);
        this.datePublished = getDateFromString(rawDatePublished);
    }

    private Date getDateFromString(String s) {
        s = s.substring(0, 9) + s.substring(11,19);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(s);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public String getDatePublishedString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(datePublished);
    }
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "News{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", content='" + content + '\'' +
                ", url=" + url.toString() +
                ", imageURL=" + imageURL.toString() +
                ", datePublished=" + formatter.format(datePublished) +
                '}';
    }
}
