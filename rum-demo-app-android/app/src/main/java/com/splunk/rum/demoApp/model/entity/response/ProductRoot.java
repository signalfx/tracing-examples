package com.splunk.rum.demoApp.model.entity.response;

import java.util.ArrayList;

public class ProductRoot {
    private ArrayList<NewProduct> products = new ArrayList<>();

    public ArrayList<NewProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<NewProduct> products) {
        this.products = products;
    }
}
