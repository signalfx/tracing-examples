package com.splunk.rum.demoApp.model.entity.request;


public class LoginRequest {
    private String email;
    private String password;
    private boolean returnSecureToken;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.returnSecureToken = true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isReturnSecureToken() {
        return returnSecureToken;
    }

    public void setReturnSecureToken(boolean returnSecureToken) {
        this.returnSecureToken = returnSecureToken;
    }
}
