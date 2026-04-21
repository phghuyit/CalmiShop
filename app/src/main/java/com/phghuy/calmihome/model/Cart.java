package com.phghuy.calmihome.model;

public class Cart {
    private int id;
    private int qty;
    private Product product;

    public Cart(int id, int qty, Product product) {
        this.id = id;
        this.qty = qty;
        this.product = product;
    }

    public int getId() { return id; }
    public int getQty() { return qty; }
    public Product getProduct() { return product; }

    public void setId(int id) { this.id = id; }
    public void setQty(int qty) { this.qty = qty; }
    public void setProduct(Product product) { this.product = product; }
}
