package com.phghuy.calmihome.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String description;
    private String price;
    private int qty;
    private String imageBase64;
    private String imageName;

    public Product(int id, String name, String description, String price, int qty, String imageName, String imageBase64) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.qty = qty;
        this.imageName = imageName;
        this.imageBase64 = imageBase64;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public int getQty() { return qty; }
    public String getImageBase64() { return imageBase64; }
    public String getImageName() { return imageName; }
}
