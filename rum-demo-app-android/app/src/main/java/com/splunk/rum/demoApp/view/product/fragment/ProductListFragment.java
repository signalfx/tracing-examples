package com.splunk.rum.demoApp.view.product.fragment;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.FragmentProductListBinding;
import com.splunk.rum.demoApp.model.entity.response.Product;
import com.splunk.rum.demoApp.model.entity.response.ProductRoot;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.home.MainActivity;
import com.splunk.rum.demoApp.view.product.adapter.ProductListAdapter;
import com.splunk.rum.demoApp.view.product.viewModel.ProductViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductListFragment extends BaseFragment {
    FragmentProductListBinding binding;
    private ArrayList<Product> productDataList;
    private ProductViewModel productViewModel;

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productDataList = new ArrayList<>();
        productDataList.addAll(generateProductList());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setupToolbar();
            ((MainActivity) getActivity()).getMainViewModel().getIsFromCart().setValue(Boolean.FALSE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        if (getActivity() != null && getActivity() instanceof BaseActivity &&
                ((MainActivity) getActivity()).getBottomNavigationView() != null) {
            ((MainActivity) getActivity()).getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);
        }

        // Configure ViewModel
        productViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(ProductViewModel.class);
        productViewModel.createView(this);
        binding.setViewModel(productViewModel);
        binding.executePendingBindings();

        // handle api call response data
        if (getActivity() != null) {
            productViewModel.getBaseResponse()
                    .observe(getActivity(),
                            handleResponse());

            productViewModel.getmIsLoading().observe(getActivity(), handleLoadingResponse());
        }

        productViewModel.getProductList();

        return binding.getRoot();
    }


    /**
     * @return Handle product list API Response
     */
    private androidx.lifecycle.Observer<ResponseBody> handleResponse() {
        return response -> {
            try {
                setUpRecyclerView();
            } catch (Exception e) {
                AppUtils.handleRumException(e);
            }
        };
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

    private void setUpRecyclerView() {
        int spanCount = 2;
        if (AppUtils.is10InchTablet(getActivity())) {
            spanCount = 3;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        ProductListAdapter productListAdapter = new ProductListAdapter(getContext(), productDataList, this);
        binding.recyclerView.setAdapter(productListAdapter);

        Span workflow = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_product_list_loaded));
        workflow.setStatus(StatusCode.OK, getString(R.string.rum_event_product_list_loaded_msg));
        workflow.end();
    }

    /**
     * @return List of products from json
     */
    private ArrayList<Product> generateProductList() {
        try {
            if (getActivity() != null && getActivity() instanceof BaseActivity) {
                AssetManager assetManager = getActivity().getAssets();
                InputStream ims = assetManager.open(AppConstant.PRODUCT_JSON_FILE_NAME);
                Gson gson = new Gson();
                Reader reader = new InputStreamReader(ims);
                ProductRoot productRoot = gson.fromJson(reader, ProductRoot.class);
                return productRoot.getProducts();
            }
        } catch (IOException e) {
            AppUtils.handleRumException(e);
        }

        return new ArrayList<>();
    }

    public ArrayList<Product> getProductList() {
        return new ArrayList<>(productDataList);
    }

    public ProductViewModel getProductViewModel() {
        return productViewModel;
    }
}