package com.stevecao.avportal.model;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Event {
    private User teacherIc;
    private String name, desc, location, id;
    private ArrayList<URL> imageUrls;
    private ArrayList<Date> startDates = new ArrayList<>(0);
    private ArrayList<Date> endDates = new ArrayList<>(0);
    private Date actualDate;
    private HashMap<Equipment, Long> equiNeeded;
    private long manpower;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Event(User teacherIc, String name, String desc, String location, String id,
                 ArrayList<URL> imageUrls, ArrayList<Date> dates, HashMap<Equipment, Long> equiNeeded,
                 long manpower) {
        this.teacherIc = teacherIc;
        this.name = name;
        this.desc = desc;
        this.location = location;
        this.id = id;
        this.imageUrls = imageUrls;
        this.equiNeeded = equiNeeded;
        this.manpower = manpower;

        for (int x = 0; x < dates.size(); x += 2) {
            startDates.add(dates.get(x));
            endDates.add(dates.get(x + 1));
        }
        actualDate = dates.get(dates.size() - 2);
    }

    public ArrayList<String> getStringStartDates() {
        ArrayList<String> result = new ArrayList<>(0);
        for (Date d : startDates) {
            result.add(formatter.format(d));
        }
        return result;
    }

    public ArrayList<String> getStringEndDates() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        ArrayList<String> result = new ArrayList<>(0);
        for (Date d : endDates) {
            result.add(formatter.format(d));
        }
        return result;
    }

    public String getStringActualDate() {
        return formatter.format(actualDate);
    }

    public User getTeacherIc() {
        return teacherIc;
    }

    public void setTeacherIc(User teacherIc) {
        this.teacherIc = teacherIc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<URL> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<URL> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public URL getMainImageUrl() {
        return imageUrls.get(0);
    }

    public ArrayList<Date> getStartDates() {
        return startDates;
    }

    public void setStartDates(ArrayList<Date> startDates) {
        this.startDates = startDates;
    }

    public ArrayList<Date> getEndDates() {
        return endDates;
    }

    public void setEndDates(ArrayList<Date> endDates) {
        this.endDates = endDates;
    }

    public Date getActualDate() {
        return actualDate;
    }

    public void setActualDate(Date actualDate) {
        this.actualDate = actualDate;
    }

    public HashMap<Equipment, Long> getEquiNeeded() {
        return equiNeeded;
    }

    public void setEquiNeeded(HashMap<Equipment, Long> equiNeeded) {
        this.equiNeeded = equiNeeded;
    }

    public long getManpower() {
        return manpower;
    }

    public void setManpower(long manpower) {
        this.manpower = manpower;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
