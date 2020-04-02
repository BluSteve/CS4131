package com.stevecao.avportal.model;

import java.net.URL;

public class Equipment {
    private String brand, name;
    private int cost, count;
    private URL imageUrl;

    public Equipment(String brand, String name, int cost, int count, URL imageUrl) {
        this.brand = brand;
        this.name = name;
        this.cost = cost;
        this.count = count;
        this.imageUrl = imageUrl;
    }
}
