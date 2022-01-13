package com.splunk.rum.demoApp.view.base.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.checkout.viewModel.CheckoutViewModel;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.product.viewModel.ProductViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final ResourceProvider resourceProvider;


    public ViewModelFactory(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }


    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckoutViewModel.class)) {
            return (T) new CheckoutViewModel(resourceProvider);
        } else if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(resourceProvider);
        } else if (modelClass.isAssignableFrom(EventViewModel.class)) {
            return (T) new EventViewModel(resourceProvider);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}