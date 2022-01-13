package com.splunk.rum.demoApp.callback;

@SuppressWarnings("unused")
public interface ViewModelListener<T> {

    void createView(T view);

    void destroyView();
}
