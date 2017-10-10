package com.misapps.oscarruiz.myshopping.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class Product {

    /**
     * Product name
     */
    @SerializedName("productName")
    private String productName;

    /**
     * Product quantity
     */
    @SerializedName("quantity")
    private int quantity;

    /**
     * Product price
     */
    @SerializedName("price")
    private double price;

    /**
     * Indicates if product has been picked up
     */
    @SerializedName("picked")
    private boolean isPicked;

    /**
     * Indicates quantity type
     */
    private int quantityType;

    public Product(String productName, int quantity, int quantityType) {
        this.productName = productName;
        this.quantity = quantity;
        this.quantityType = quantityType;
    }

    public boolean isPicked() { return isPicked; }

    public void setPicked(boolean picked) { isPicked = picked; }

    public Product(String productName) {
        this.productName = productName;
    }

    public String getProductName() { return productName; }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(int quantityType) {
        this.quantityType = quantityType;
    }
}
