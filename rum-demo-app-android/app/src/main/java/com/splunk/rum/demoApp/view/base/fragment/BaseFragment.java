package com.splunk.rum.demoApp.view.base.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;
import com.splunk.rum.demoApp.callback.ViewListener;
import com.splunk.rum.demoApp.network.RetrofitException;
import com.splunk.rum.demoApp.util.AlertDialogHelper;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.view.cart.fragment.ShoppingCartFragment;
import com.splunk.rum.demoApp.view.event.fragment.EventGenerationFragment;
import com.splunk.rum.demoApp.view.product.fragment.ProductDetailsFragment;
import com.splunk.rum.demoApp.view.product.fragment.ProductListFragment;

public class BaseFragment extends Fragment implements ViewListener, DialogButtonClickListener {

    private boolean isCart = false;
    private boolean is4xx = false;
    private boolean is5xx = false;
    private boolean isSlowAPI = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unused")
    @Override
    public void showProgress() {
    }

    @SuppressWarnings("unused")
    @Override
    public void hideProgress() {
    }

    @Override
    public boolean isNetworkAvailable() {
        if (getContext() != null) {
            return AppUtils.isNetworkAvailable(getContext());
        } else {
            return AppUtils.isNetworkAvailable(requireActivity());
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void showApiError(RetrofitException retrofitException, String errorCode) {
        hideProgress();
        if (getActivity() != null) {
            if (errorCode.equalsIgnoreCase(AppConstant.ERROR_INTERNET)
                    && retrofitException != null
                    && retrofitException.getMessage() != null) {
                isCart = retrofitException.getMessage().equalsIgnoreCase(getString(R.string.rum_event_add_to_cart));
                is4xx = retrofitException.getMessage().equalsIgnoreCase(getString(R.string.method_not_found));
                is5xx = retrofitException.getMessage().equalsIgnoreCase(getString(R.string.http_error));
                isSlowAPI = retrofitException.getMessage().equalsIgnoreCase(getString(R.string.slow_api));
                AlertDialogHelper.showDialog(getActivity(), null, getString(R.string.error_network)
                        , getString(R.string.ok), getString(R.string.retry), false,
                        this, AppConstant.DialogIdentifier.INTERNET_DIALOG);
            } else {
                AppUtils.handleApiError(getActivity(), retrofitException);
            }

            if (this instanceof ProductDetailsFragment) {
                AppUtils.enableDisableBtn(true,
                        ((ProductDetailsFragment) this).getBinding().btnAddToCart);
            }

        }
    }


    @Override
    public void onPositiveButtonClicked(int dialogIdentifier) {
    }

    @Override
    public void onNegativeButtonClicked(int dialogIdentifier) {
        if (dialogIdentifier == AppConstant.DialogIdentifier.INTERNET_DIALOG) {
            if (this instanceof ProductListFragment) {
                ((ProductListFragment) this).getProductViewModel().getProductList();
            } else if (this instanceof ProductDetailsFragment) {
                if (isCart) {
                    ((ProductDetailsFragment) this).getProductViewModel().addToCart(String.valueOf(((ProductDetailsFragment) this).getProductDetails().getQuantity()),
                            ((ProductDetailsFragment) this).getProductDetails().getId());
                } else {
                    ((ProductDetailsFragment) this).getProductViewModel().getProductDetail(((ProductDetailsFragment) this).getProductDetails().getId());
                }


                if (is4xx) {
                    ((ProductDetailsFragment) this).getEventViewModel().generateHttpNotFound();
                }

                if (is5xx) {
                    ((ProductDetailsFragment) this).getEventViewModel().generateHttpError(((ProductDetailsFragment) this).getProductDetails().getId(), 1);
                }

            } else if (this instanceof ShoppingCartFragment) {
                if (isSlowAPI) {
                    ((ShoppingCartFragment) this).getViewModel().slowApiResponse();
                } else {
                    ((ShoppingCartFragment) this).getProductViewModel().getCartItems();
                }
            } else if (this instanceof EventGenerationFragment) {
                if (is4xx) {
                    ((EventGenerationFragment) this).getViewModel().generateHttpNotFound();
                }

                if (is5xx) {
                    ((EventGenerationFragment) this).getViewModel().generateHttpError("", 0);
                }

                if (isSlowAPI) {
                    ((EventGenerationFragment) this).getViewModel().slowApiResponse();
                }
            }
        }
    }
}
