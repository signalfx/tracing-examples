package com.splunk.rum.demoApp.model.entity.request;


public class CheckoutRequest {
    private String email;
    private String address;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private String creditCardNumber;
    private int month;
    private String monthName;
    private String year;
    private String cvv;

    public CheckoutRequest(String email, String address, String zipCode, String city, String state,
                           String country, String creditCardNumber, int month, String year, String cvv,String monthName) {
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.country = country;
        this.creditCardNumber = creditCardNumber;
        this.month = month;
        this.year = year;
        this.cvv = cvv;
        this.monthName = monthName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public CheckoutRequest(int month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
