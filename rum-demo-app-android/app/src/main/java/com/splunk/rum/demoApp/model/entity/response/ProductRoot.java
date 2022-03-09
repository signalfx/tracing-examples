package com.splunk.rum.demoApp.model.entity.response;

import java.util.ArrayList;
@SuppressWarnings("ALL")
public class ProductRoot {
    private ArrayList<Product> products = new ArrayList<>();

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
