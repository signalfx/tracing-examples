package com.splunk.rum.demoApp.view.cart.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;
import com.splunk.rum.demoApp.databinding.FragmentShoppingCartBinding;
import com.splunk.rum.demoApp.model.entity.response.Product;
import com.splunk.rum.demoApp.util.AlertDialogHelper;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.PreferenceHelper;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.cart.adapter.CartProductListAdapter;
import com.splunk.rum.demoApp.view.checkout.activity.CheckOutActivity;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.home.MainActivity;
import com.splunk.rum.demoApp.view.product.viewModel.ProductViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
@SuppressWarnings("ALL")
public class ShoppingCartFragment extends BaseFragment implements DialogButtonClickListener {
    private FragmentShoppingCartBinding binding;
    private boolean isFromProductDetail;
    private CartProductListAdapter productListAdapter;
    private int totalItem;
    private EventViewModel viewModel;
    private ProductViewModel productViewModel;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isFromProductDetail = getArguments().getBoolean(AppConstant.IntentKey.IS_FROM_PRODUCT_DETAIL, false);
        }

        if (getActivity() instanceof BaseActivity && isFromProductDetail) {
            ((BaseActivity) getActivity()).setupToolbar(true);
        } else if (getActivity() instanceof BaseActivity && !isFromProductDetail) {
            ((BaseActivity) getActivity()).setupToolbar();
        }

        // Configure ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(EventViewModel.class);
        viewModel.createView(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);
        if (getActivity() != null && getActivity() instanceof BaseActivity &&
                ((MainActivity) getActivity()).getBottomNavigationView() != null) {
            ((MainActivity) getActivity()).getBottomNavigationView().getMenu().findItem(R.id.navigation_cart).setChecked(true);
        }

        // Configure ViewModel
        productViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(ProductViewModel.class);
        productViewModel.createView(this);
        binding.setViewModel(productViewModel);
        binding.setEventViewModel(viewModel);
        binding.executePendingBindings();


        // handle api call response data
        if (getActivity() != null) {
            productViewModel.getBaseResponse()
                    .observe(getActivity(),
                            handleCartItemsResponse());

            productViewModel.getmIsLoading().observe(getActivity(), handleLoadingResponse());
            viewModel.getmIsLoading().observe(getActivity(), handleLoadingResponse());
        }




        productViewModel.getCartItems();

        binding.btnCheckout.setOnClickListener(view -> navigateToCheckout());


        binding.btnBrowseProduct.setOnClickListener(view -> {
            NavHostFragment.findNavController(ShoppingCartFragment.this).navigate(R.id.action_navigation_cart_to_navigation_home);
            if (getActivity() instanceof MainActivity && ((MainActivity) getActivity()).getBottomNavigationView() != null) {
                ((MainActivity) getActivity()).getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);
            }
        });

        binding.btnEmptyCart.setOnClickListener(view -> AlertDialogHelper.showDialog(getContext(), null, getString(R.string.alert_empty_cart_msg), getString(R.string.yes), getString(R.string.no), true, this, AppConstant.DialogIdentifier.EMPTY_CART));

        return binding.getRoot();
    }

    private void navigateToCheckout(){
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            if(AppUtils.isNetworkAvailable(getActivity())){
                ((BaseActivity) getActivity()).moveActivity(getActivity(), CheckOutActivity.class, false);
            }else{
                AlertDialogHelper.showDialog(getActivity(), null, getString(R.string.error_network)
                        , getString(R.string.ok), getString(R.string.retry), false,
                        this, AppConstant.DialogIdentifier.CHECK_OUT_DIALOG);
            }
        }
    }

    /**
     * @return show hider progressbar based on  isLoading boolean value
     */
    private androidx.lifecycle.Observer<Boolean> handleLoadingResponse() {
        return isLoading -> {
            try {
                AppUtils.showHideLoader(isLoading,binding.progressBar.progressLinearLayout,binding.parentLayout);
            } catch (Exception e) {
                AppUtils.handleRumException(e);
            }
        };
    }

    /**
     * @return Handle get cart items response
     */
    private androidx.lifecycle.Observer<ResponseBody> handleCartItemsResponse() {
        return response -> {
            try {
                setUpNoDataFound();
                calculateTotalCost();
                setUpRecyclerView();
            } catch (Exception e) {
                AppUtils.handleRumException(e);
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        setUpNoDataFound();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getMainViewModel().getIsFromCart().setValue(Boolean.TRUE);
        }
    }

    private void calculateTotalCost() {
        double totalCost = 0.0;
        totalItem = 0;
        if (!CollectionUtils.isEmpty(AppUtils.getProductsFromPref(getActivity()).getProducts())) {
            for (Product product : AppUtils.getProductsFromPref(getActivity()).getProducts()) {
                totalCost += (product.getQuantity() * product.getPriceUsd().getPrice());
                totalItem += product.getQuantity();
            }
        }
        String formattedTotalCost = new DecimalFormat("##.##").format(totalCost);
        binding.tvTotalCost.setText(formattedTotalCost);
    }

    private void setUpRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<Product> arrayList = new ArrayList<>(AppUtils.getProductsFromPref(getActivity()).getProducts());

        productListAdapter = new CartProductListAdapter(getContext(), arrayList);
        binding.recyclerView.setAdapter(productListAdapter);

        if (!CollectionUtils.isEmpty(AppUtils.getProductsFromPref(getActivity()).getProducts())
                && totalItem > 1) {
            String text = String.format(getResources().getString(R.string.items_in_cart), totalItem);
            binding.totalItemsInCart.setText(text);
        } else {
            String text = String.format(getResources().getString(R.string.item_in_cart), totalItem);
            binding.totalItemsInCart.setText(text);
        }
    }


    private void setUpNoDataFound() {
        if (getActivity() != null && (CollectionUtils.isEmpty(AppUtils.getProductsFromPref(getActivity()).getProducts())
                || !AppUtils.isNetworkAvailable(getActivity()))) {
            binding.noDataLayout.setVisibility(View.VISIBLE);
            binding.contentLayout.setVisibility(View.GONE);
        } else {
            binding.noDataLayout.setVisibility(View.GONE);
            binding.contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPositiveButtonClicked(int dialogIdentifier) {
        if (dialogIdentifier == AppConstant.DialogIdentifier.EMPTY_CART) {

            viewModel.slowApiResponse();

            AppUtils.getProductsFromPref(getActivity()).getProducts().clear();
            PreferenceHelper.removeKey(getActivity(),AppConstant.SharedPrefKey.CART_PRODUCTS);
            productListAdapter.clear();
            setUpNoDataFound();

            if (getActivity() instanceof MainActivity
                    && ((MainActivity) getActivity()).getBottomNavigationView() != null) {
                BadgeDrawable badge = ((MainActivity) getActivity()).getBottomNavigationView().getOrCreateBadge(R.id.navigation_cart);
                badge.setVisible(false);
                badge.clearNumber();
            }
        }else{
            super.onPositiveButtonClicked(dialogIdentifier);
        }
    }

    @Override
    public void onNegativeButtonClicked(int dialogIdentifier) {
        if(dialogIdentifier == AppConstant.DialogIdentifier.CHECK_OUT_DIALOG){
            navigateToCheckout();
        }else{
            super.onNegativeButtonClicked(dialogIdentifier);
        }
    }

    public ProductViewModel getProductViewModel() {
        return productViewModel;
    }

    public EventViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(EventViewModel viewModel) {
        this.viewModel = viewModel;
    }
}