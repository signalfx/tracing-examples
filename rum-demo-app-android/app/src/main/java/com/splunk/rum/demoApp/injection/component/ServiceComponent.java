package com.splunk.rum.demoApp.injection.component;


import com.splunk.rum.demoApp.injection.module.CheckoutModule;
import com.splunk.rum.demoApp.injection.module.EventModule;
import com.splunk.rum.demoApp.injection.module.ProductModule;
import com.splunk.rum.demoApp.injection.scope.UserScope;
import com.splunk.rum.demoApp.view.checkout.viewModel.CheckoutViewModel;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.product.viewModel.ProductViewModel;

import dagger.Component;

@UserScope
@Component(dependencies = com.splunk.rum.demoApp.injection.component.NetworkComponent.class,
        modules = {CheckoutModule.class, ProductModule.class,
                EventModule.class})

public interface ServiceComponent {
    // for authentication view model

    // for checkout model
    void inject(CheckoutViewModel checkoutViewModel);

    // for product model
    void inject(ProductViewModel productViewModel);

    // for event model
    void inject(EventViewModel eventViewModel);
}
