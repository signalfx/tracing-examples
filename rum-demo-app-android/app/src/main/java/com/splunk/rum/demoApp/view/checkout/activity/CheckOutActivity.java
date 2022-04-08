package com.splunk.rum.demoApp.view.checkout.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.PolyUtil;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.BuildConfig;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.ActivityCheckOutBinding;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.util.ValidationUtil;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.checkout.viewModel.CheckoutViewModel;
import com.splunk.rum.demoApp.view.order.activity.OrderDetailActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import okhttp3.ResponseBody;


public class CheckOutActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCheckOutBinding binding;
    private CheckoutViewModel checkoutViewModel;
    private Context mContext;
    private ArrayList<TextInputEditText> textInputEditTextList;
    private ArrayList<TextInputLayout> textInputLayoutList;
    private ArrayList<String> errorMessageList;
    private String countryName;

    public CheckoutViewModel getCheckoutViewModel() {
        return checkoutViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;


        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out);
        setupToolbar(true);
        createListOfComponent();

        checkCountryNameIsEmpty();

        //Click Listener
        binding.edtYear.setOnClickListener(this);
        binding.edtMonth.setOnClickListener(this);


        // Configure ViewModel
        checkoutViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(CheckoutViewModel.class);
        binding.setCheckoutViewModel(checkoutViewModel);
        checkoutViewModel.createView(this);
        binding.setLifecycleOwner(this);

        Span workflow = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_checkout_viewed));
        workflow.setStatus(StatusCode.OK, getString(R.string.rum_event_checkout_viewed_msg));
        workflow.end();

        checkoutViewModel.getBaseResponse()
                .observe(this,
                        handleResponse());


        binding.btnPlaceOrder.setOnClickListener(view -> {
            boolean isFormValid = false;
            hideKeyboard(this);
            for (int i = 0; i < textInputEditTextList.size(); i++) {
                if (StringHelper.isEmpty(Objects.requireNonNull(textInputEditTextList.get(i).getText()).toString())) {
                    ValidationUtil.setErrorIntoInputTextLayout(textInputEditTextList.get(i), textInputLayoutList.get(i), errorMessageList.get(i));
                    if (i <= 4) {
                        scrollToTop();
                    }
                    isFormValid = false;
                    break;
                } else {
                    ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(i));

                    // check for further email validation
                    if (textInputEditTextList.get(i).getId() == R.id.edtEmail
                            && !ValidationUtil.isValidEmail(checkoutViewModel.getCheckoutRequest().getEmail())) {
                        scrollToTop();
                        ValidationUtil.setErrorIntoInputTextLayout(textInputEditTextList.get(i),
                                textInputLayoutList.get(i),
                                mContext.getString(R.string.error_email));
                        isFormValid = false;
                        break;
                    } else {
                        ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(i));
                    }


                    // check for further zip code validation
                    if (textInputEditTextList.get(i).getId() == R.id.edtZip
                            && !ValidationUtil.isValidZipCode(checkoutViewModel.getCheckoutRequest().getZipCode())) {
                        ValidationUtil.setErrorIntoInputTextLayout(textInputEditTextList.get(i),
                                textInputLayoutList.get(i),
                                mContext.getString(R.string.error_format_format));
                        scrollToTop();
                        isFormValid = false;
                        break;
                    } else {
                        ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(i));
                    }
                    if (textInputEditTextList.get(i).getId() == R.id.edtCvv
                            && !ValidationUtil.isValidCvv(checkoutViewModel.getCheckoutRequest().getCvv())) {
                        ValidationUtil.setErrorIntoInputTextLayout(textInputEditTextList.get(i),
                                textInputLayoutList.get(i),
                                mContext.getString(R.string.error_format_format));
                        isFormValid = false;
                        break;
                    } else {
                        ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(i));
                    }

                    if (textInputEditTextList.get(i).getId() == R.id.edtCreditCard
                            && !ValidationUtil.isValidCard(checkoutViewModel.getCheckoutRequest().getCreditCardNumber())) {
                        ValidationUtil.setErrorIntoInputTextLayout(textInputEditTextList.get(i),
                                textInputLayoutList.get(i),
                                mContext.getString(R.string.error_format_format));
                        isFormValid = false;
                        break;
                    } else {
                        ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(i));
                    }

                    isFormValid = true;
                }
            }

            if (isFormValid) {
                Span payment = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_place_order));
                payment.setStatus(StatusCode.OK, getString(R.string.rum_event_place_order_msg));
                payment.end();

                if (StringHelper.isNotEmpty(getCountryName()) && getCountryName().equalsIgnoreCase(BuildConfig.COUNTRY_NAME_FRANCE)) {
                    paymentFailCustomEvent(getString(R.string.rum_event_payment_france_msg));
                    checkoutViewModel.generateNewSalesTax();
                } else {
                    if (checkoutViewModel.getCheckoutRequest().getCreditCardNumber().equalsIgnoreCase(AppConstant.FAKE_CC_NUMBER)) {
                        paymentFailCustomEvent(getString(R.string.rum_event_payment_fail_msg));
                        return;
                    }
                    AppUtils.enableDisableBtn(false,binding.btnPlaceOrder);
                    checkoutViewModel.doCheckOut();
                }
            }
        });
    }

    private void paymentFailCustomEvent(String message) {
        Span paymentFail = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_payment_fail));
        paymentFail.setAttribute("error", true);
        paymentFail.setStatus(StatusCode.ERROR, message);
        paymentFail.end();
        AppUtils.showError(mContext, message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mCountryNameReceiver,
                        new IntentFilter(AppConstant.IntentKey.INTENT_KEY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mCountryNameReceiver);
    }

    /*
      Broadcast Receiver to receive country name from location  service
    */
    private final BroadcastReceiver mCountryNameReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String name = intent.getStringExtra(AppConstant.IntentKey.COUNTRY_NAME);
            if (StringHelper.isNotEmpty(name)) {
                setCountryName(name);
            } else if (StringHelper.isEmpty(getCountryName())) {
                checkCountryNameIsEmpty();
            }
        }
    };

    /**
     * Check country name is set null from location service and based on check country is france or not
     */
    private void checkCountryNameIsEmpty() {
        Boolean isCountryNameEmpty = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.IS_COUNTRY_NAME_EMPTY,
                Boolean.class, false);
        if (isCountryNameEmpty) {
            String lat = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.LAT,
                    String.class, "");
            String lon = PreferenceHelper.getValue(this, AppConstant.SharedPrefKey.LNG,
                    String.class, "");
            if (StringHelper.isNotEmpty(lat) && StringHelper.isNotEmpty(lon)) {
                LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                List<LatLng> latLngList = new ArrayList<>();
                latLngList.add(AppConstant.PolygonPoint.POINT_1);
                latLngList.add(AppConstant.PolygonPoint.POINT_2);
                latLngList.add(AppConstant.PolygonPoint.POINT_3);
                latLngList.add(AppConstant.PolygonPoint.POINT_4);
                latLngList.add(AppConstant.PolygonPoint.POINT_5);
                boolean isCoordinateInPolygon = PolyUtil.containsLocation(latLng, latLngList, true);
                if (isCoordinateInPolygon) setCountryName(BuildConfig.COUNTRY_NAME_FRANCE);
            }
        }
    }


    /**
     * @return Handle checkout API Response
     */
    private androidx.lifecycle.Observer<ResponseBody> handleResponse() {
        return response -> {
            AppUtils.enableDisableBtn(true,binding.btnPlaceOrder);
            try {
                if (response != null && StringHelper.isNotEmpty(response.toString())) {
                    moveActivity(this, OrderDetailActivity.class, true, true);
                }
            } catch (Exception e) {
                AppUtils.handleRumException(e);
            }
        };
    }

    /*
        Create this method for validation
     */
    private void createListOfComponent() {
        // EditText List
        textInputEditTextList = new ArrayList<>();
        textInputEditTextList.add(binding.edtEmail);
        textInputEditTextList.add(binding.edtAddress);
        textInputEditTextList.add(binding.edtZip);
        textInputEditTextList.add(binding.edtCountry);
        textInputEditTextList.add(binding.edtState);
        textInputEditTextList.add(binding.edtCity);
        textInputEditTextList.add(binding.edtCreditCard);
        textInputEditTextList.add(binding.edtMonth);
        textInputEditTextList.add(binding.edtYear);
        textInputEditTextList.add(binding.edtCvv);

        // TextLayout List
        textInputLayoutList = new ArrayList<>();
        textInputLayoutList.add(binding.emailTextField);
        textInputLayoutList.add(binding.addressTextField);
        textInputLayoutList.add(binding.zipTextField);
        textInputLayoutList.add(binding.countryTextField);
        textInputLayoutList.add(binding.stateTextField);
        textInputLayoutList.add(binding.cityTextField);
        textInputLayoutList.add(binding.creditCardTextField);
        textInputLayoutList.add(binding.monthTextField);
        textInputLayoutList.add(binding.yearTextField);
        textInputLayoutList.add(binding.cvvTextField);

        // TextLayout List
        errorMessageList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            errorMessageList.add(mContext.getString(R.string.error_black_validation));
        }

        for (int i = 0; i < textInputEditTextList.size(); i++) {
            textInputEditTextList.get(i).addTextChangedListener(new TextFieldValidation(i));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edtYear) {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(mContext, binding.edtYear);

            for (int i = Calendar.getInstance().get(Calendar.YEAR); i <= Calendar.getInstance().get(Calendar.YEAR) + 4; i++) {
                popupMenu.getMenu().add(1, i, i, String.valueOf(i));
            }

            for(int i = 0; i< popupMenu.getMenu().size(); i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    popupMenu.getMenu().getItem(i).setContentDescription(String.format(getString(R.string.checkout_year_),
                            popupMenu.getMenu().getItem(i)));
                }
            }

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.year_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                checkoutViewModel.getCheckoutRequest().setYear(menuItem.getTitle().toString());
                binding.edtYear.setText(String.valueOf(menuItem.getTitle()));
                return true;

            });
            // Showing the popup menu
            popupMenu.show();
        } else if (view.getId() == R.id.edtMonth) {
            // Initializing the popup menu and giving the reference as current context
            PopupMenu popupMenu = new PopupMenu(mContext, binding.edtMonth);

            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.month_menu, popupMenu.getMenu());

            for(int i = 0; i< popupMenu.getMenu().size(); i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    popupMenu.getMenu().getItem(i).setContentDescription(String.format(getString(R.string.checkout_month_),
                            popupMenu.getMenu().getItem(i)));
                }
            }

            popupMenu.setOnMenuItemClickListener(menuItem -> {
                checkoutViewModel.getCheckoutRequest().setMonth(menuItem.getItemId());
                checkoutViewModel.getCheckoutRequest().setMonthName(menuItem.getTitle().toString());
                binding.edtMonth.setText(menuItem.getTitle().toString());
                return true;
            });
            // Showing the popup menu
            popupMenu.show();
        }
    }

    /**
     * TextWatcher for all the textInputField
     */
    private class TextFieldValidation implements TextWatcher {
        private final int position;
        private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
        private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
        private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
        private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
        private static final char DIVIDER = '-';

        public TextFieldValidation(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void afterTextChanged(Editable editable) {
            if (StringHelper.isNotEmpty(Objects.requireNonNull(textInputEditTextList
                    .get(position).getText()).toString())) {
                ValidationUtil.removeErrorFromTextLayout(textInputLayoutList.get(position));

                //// https://stackoverflow.com/questions/11790102/format-credit-card-in-edit-text-in-android
                if (textInputEditTextList.get(position).getId() == R.id.edtCreditCard
                        && !isInputCorrect(editable)) {
                    editable.replace(0, editable.length(),
                            buildCorrectString(getDigitArray(editable)));
                }
            }
        }
    }

    // https://stackoverflow.com/questions/11790102/format-credit-card-in-edit-text-in-android
    private boolean isInputCorrect(Editable s) {
        boolean isCorrect = s.length() <= TextFieldValidation.TOTAL_SYMBOLS; // check size of entered string
        for (int i = 0; i < s.length(); i++) { // check that every element is right
            if (i > 0 && (i + 1) % TextFieldValidation.DIVIDER_MODULO == 0) {
                isCorrect &= TextFieldValidation.DIVIDER == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    // https://stackoverflow.com/questions/11790102/format-credit-card-in-edit-text-in-android
    private String buildCorrectString(char[] digits) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % TextFieldValidation.DIVIDER_POSITION) == 0)) {
                    formatted.append(TextFieldValidation.DIVIDER);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s) {
        char[] digits = new char[TextFieldValidation.TOTAL_DIGITS];
        int index = 0;
        for (int i = 0; i < s.length() && index < TextFieldValidation.TOTAL_DIGITS; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }

    private void scrollToTop() {
        binding.scrollView.post(() -> binding.scrollView.smoothScrollTo(0, 0));
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ActivityCheckOutBinding getBinding() {
        return binding;
    }
}
