package com.stevecao.avportal.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Event {
    private Person teacherIc;
    private String name, desc;
    private ArrayList<URL> imageUrls = new ArrayList<>(0);
    private URL mainImageUrl;
    private Date date;
    private HashMap<Equipment, Integer> equiNeeded;
    private int manpower;

    public Event(Person teacherIc, String name, String desc, ArrayList<URL> imageUrls,
                 URL mainImageUrl, Date date, HashMap<Equipment, Integer> equiNeeded, int manpower) {
        this.teacherIc = teacherIc;
        this.name = name;
        this.desc = desc;
        this.imageUrls = imageUrls;
        this.mainImageUrl = mainImageUrl;
        this.date = date;
        this.equiNeeded = equiNeeded;
        this.manpower = manpower;
    }
}

