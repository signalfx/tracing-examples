package com.splunk.rum.demoApp.model.entity.response;

import org.parceler.Parcel;

import java.util.ArrayList;
@SuppressWarnings("ALL")
@Parcel
public class Product {
    String id;
    String name;
    String description;
    String picture;
    String errorType;
    String errorAction;
    String availableQty;
    PriceUsd priceUsd;
    ArrayList<String> categories;
    int quantity;

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public PriceUsd getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(PriceUsd priceUsd) {
        this.priceUsd = priceUsd;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorAction() {
        return errorAction;
    }

    public void setErrorAction(String errorAction) {
        this.errorAction = errorAction;
    }

    public String getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(String availableQty) {
        this.availableQty = availableQty;
    }
}
