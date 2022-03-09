package com.splunk.rum.demoApp.callback;

@SuppressWarnings("ALL")
public interface ViewModelListener<T> {

    void createView(T view);

    void destroyView();
}
