package com.stevecao.avportal.model;

public class StageAction {
    String id, title, notif, ttstext;
    float x, y;

    public StageAction(String id, String title, String notif, String ttstext, float x, float y) {
        this.id = id;
        this.title = title;
        this.notif = notif;
        this.ttstext = ttstext;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotif() {
        return notif;
    }

    public void setNotif(String notif) {
        this.notif = notif;
    }

    public String getTtstext() {
        return ttstext;
    }

    public void setTtstext(String ttstext) {
        this.ttstext = ttstext;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String toString() {
        return id + "|" + title + "|" + notif + "|" + ttstext + "|" + x + "|" + y;
    }
}