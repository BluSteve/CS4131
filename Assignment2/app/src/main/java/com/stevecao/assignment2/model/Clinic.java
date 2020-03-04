package com.stevecao.assignment2.model;

import com.google.firebase.firestore.GeoPoint;

public class Clinic {
    private String name, address, phoneNo, type;
    private GeoPoint geoPoint;

    public Clinic(String name, String address, String phoneNo, GeoPoint geoPoint, String type) {
        this.name = name;
        this.address = address;
        this.phoneNo = phoneNo;
        this.type = type;
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    @Override
    public String toString() {
        return "Clinic{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", type='" + type + '\'' +
                ", geoPoint=" + geoPoint +
                '}';
    }
}
