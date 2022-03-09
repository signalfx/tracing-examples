package com.splunk.rum.demoApp.callback;


import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressWarnings("ALL")
public class BaseViewModelListener<T> extends ViewModel implements ViewModelListener<T> {

    public T view = null;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void createView(T v) {
        this.view = v;
    }

     void bindToLifecycle(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void destroyView() {
        compositeDisposable.clear();
        view = null;
    }
}
