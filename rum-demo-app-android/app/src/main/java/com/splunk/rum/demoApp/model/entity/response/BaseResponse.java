package com.splunk.rum.demoApp.model.entity.response;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class BaseResponse {
    @SerializedName("IsSuccess")
    private boolean isSuccess;
    @SerializedName("Message")
    private String message;
    @SerializedName("status")
    private boolean status;
    @SerializedName("ErrorCode")
    private int errorCode;
    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("description")
    private String description;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
