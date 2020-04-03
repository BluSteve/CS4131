package com.stevecao.avportal.model;

import java.net.URL;

public class Equipment {
    private String brand, name, desc;
    private long cost, count;
    private URL imageUrl;

    public Equipment(String brand, String name) {
        this.brand = brand;
        this.name = name;
    }

    public Equipment(String brand, String name, String desc, long cost, long count, URL imageUrl) {
        this.brand = brand;
        this.name = name;
        this.desc = desc;
        this.cost = cost;
        this.count = count;
        this.imageUrl = imageUrl;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "brand='" + brand + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", cost=" + cost +
                ", count=" + count +
                ", imageUrl=" + imageUrl +
                '}';
    }
}