package com.splunk.rum.demoApp.model.entity.response;

import org.parceler.Parcel;

@Parcel
public class PriceUsd{
    String currencyCode;
    int units;
    int nanos;
    double price;

    public PriceUsd() {
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getNanos() {
        return nanos;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}