package com.splunk.rum.demoApp.network;


import androidx.annotation.NonNull;

import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("ALL")
public abstract class RXRetroManager<T> {

    public RXRetroManager() {
    }

    protected abstract void onSuccess(T response);

    protected void onFailure(RetrofitException retrofitException, String errorCode) {
    }

    public void rxSingleCall(Observable<T> observable) {
        Observable<DisposableObserver<T>> disposable = Observable.just(observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<T>() {
                    @Override
                    public void onNext(@NonNull T value) {
                        onSuccess(value);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        try {
                            AppUtils.handleRumException(e);
                            if (e instanceof RetrofitException) {
                                onFailure((RetrofitException) e, "");
                            } else {
                                onFailure(null, AppConstant.ERROR_UNKNOWN);
                            }
                        } catch (Exception ex) {
                            AppUtils.handleRumException(ex);
                            onFailure(null, AppConstant.ERROR_UNKNOWN);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

}
