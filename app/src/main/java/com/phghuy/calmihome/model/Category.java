package com.phghuy.calmihome.model;

public class Category {
    private int id;
    private String category;
    private String imageName;
    private String imageBase64;

    public Category(int id,String category, String imageName, String imageBase64) {
    this.id=id;
    this.category=category;
    this.imageName=imageName;
    this.imageBase64=imageBase64;
    }

    public int getId(){return id;}
    public String getCategory(){return category;}
    public String getImageName(){return imageName;}
    public String getImageBase64(){return imageBase64;}

}
