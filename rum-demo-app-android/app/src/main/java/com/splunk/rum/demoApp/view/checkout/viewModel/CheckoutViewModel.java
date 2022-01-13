package com.splunk.rum.demoApp.view.checkout.viewModel;

import com.splunk.rum.demoApp.model.entity.request.CheckoutRequest;
import com.splunk.rum.demoApp.util.MonthUtil;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.viewModel.BaseViewModel;

import java.util.Calendar;


public class CheckoutViewModel extends BaseViewModel {

    private CheckoutRequest checkoutRequest;

    public CheckoutViewModel(ResourceProvider resourceProvider) {
        int initMonth = Calendar.getInstance().get(Calendar.MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String currentMonthSortName = MonthUtil.getShortMonthNameList().get(initMonth);
        this.checkoutRequest = new CheckoutRequest(
        "someone@example.com","1600 Amphitheatre Parkway","94043","Mountain View","CA","United States","4432801561520454",1,String.valueOf(year),"672",currentMonthSortName
        );
    }

    public CheckoutRequest getCheckoutRequest() {
        return checkoutRequest;
    }

    public void setCheckoutRequest(CheckoutRequest checkoutRequest) {
        this.checkoutRequest = checkoutRequest;
    }
}
