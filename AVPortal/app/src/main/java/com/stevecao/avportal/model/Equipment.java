package com.stevecao.avportal.model;

import java.net.URL;
import java.util.ArrayList;

public class Equipment {
    private String brand, name, desc, id;
    private long cost, quantity;
    private URL mainImageUrl;
    private ArrayList<URL> imageUrls;

    public Equipment(String brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public Equipment(String brand, String name, String desc, long cost, long quantity, ArrayList<URL> imageUrls,
                     String id) {
        this.brand = brand;
        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.quantity = quantity;
        this.mainImageUrl = imageUrls.get(0);
        this.imageUrls = imageUrls;
        this.id = id;
    }

    public String getFullName() {
        return brand + " - " + name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
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

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public URL getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(URL mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<URL> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<URL> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", cost=" + cost +
                ", quantity=" + quantity +
                ", mainImageUrl=" + mainImageUrl +
                ", imageUrls=" + imageUrls +
                '}';
    }
}