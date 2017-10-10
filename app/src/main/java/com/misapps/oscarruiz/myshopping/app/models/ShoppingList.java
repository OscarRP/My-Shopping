package com.misapps.oscarruiz.myshopping.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Oscar Ruiz on 16/08/2017.
 */

public class ShoppingList {

    /**
     * List of products
     */
    @SerializedName("products")
    private ArrayList<Product> products;

    /**
     * Shopping list name
     */
    @SerializedName("name")
    private String name;

    /**
     * Shopping list creation date
     */
    @SerializedName("creationDate")
    private String creationDate;

    public ShoppingList(ArrayList<Product> products, String name, String creationDate) {
        this.products = products;
        this.name = name;
        this.creationDate = creationDate;
    }

    public ShoppingList(ArrayList<Product> products, String name) {
        this.products = products;
        this.name = name;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
