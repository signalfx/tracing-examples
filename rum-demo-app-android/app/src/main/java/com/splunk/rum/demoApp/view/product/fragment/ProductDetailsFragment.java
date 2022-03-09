package com.splunk.rum.demoApp.view.product.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.badge.BadgeDrawable;
import com.splunk.rum.SplunkRum;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.ViewListener;
import com.splunk.rum.demoApp.databinding.FragmentProductDetailsBinding;
import com.splunk.rum.demoApp.model.entity.response.Product;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.fragment.BaseFragment;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.event.viewModel.EventViewModel;
import com.splunk.rum.demoApp.view.home.MainActivity;
import com.splunk.rum.demoApp.view.product.adapter.ProductListAdapter;
import com.splunk.rum.demoApp.view.product.viewModel.ProductViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductDetailsFragment extends BaseFragment implements ViewListener {

    FragmentProductDetailsBinding binding;
    private Product productDetails;
    private ArrayList<Product> productList = new ArrayList<>();
    private ArrayList<Product> otherProductList = new ArrayList<>();
    private Context mContext;
    private ProductViewModel productViewModel;
    private EventViewModel eventViewModel;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otherProductList = new ArrayList<>();
            productDetails = Parcels.unwrap(getArguments().getParcelable(AppConstant.IntentKey.PRODUCT_DETAILS));
            productList = Parcels.unwrap(getArguments().getParcelable(AppConstant.IntentKey.PRODUCT_ARRAY));

            Collections.shuffle(productList);
            for (Product product : productList) {
                if (!product.getId().equalsIgnoreCase(productDetails.getId()) && otherProductList.size() < 4) {
                    otherProductList.add(product);
                }
            }
        }

        productViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(ProductViewModel.class);
        productViewModel.createView(this);

        // Configure ViewModel
        eventViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(EventViewModel.class);
        eventViewModel.createView(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setupToolbar(true);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false);


        // Configure ViewModel

        binding.setViewModel(productViewModel);
        binding.setEventViewModel(eventViewModel);
        binding.executePendingBindings();
        productViewModel.getProductDetail(productDetails.getId());

        //get product detail data
        productViewModel.getBaseResponse()
                .observe(getViewLifecycleOwner(),
                        responseBody());

        // handle api call response data
        productViewModel.getAddProductToCartResponse()
                .observe(getViewLifecycleOwner(),
                        handleAddProductToCartResponse());


        binding.btnAddToCart.setOnClickListener(view -> {
            if (productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_EXCEPTION)
                    && productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_ADD_PRODUCT)) {
                if (productDetails.getQuantity() > Integer.parseInt(productDetails.getAvailableQty())) {
                    try {
                        throw new RuntimeException(getString(R.string.rum_event_app_msg));
                    } catch (Exception e) {
                        AppUtils.showError(mContext, getString(R.string.rum_event_app_msg));
                        AppUtils.handleRumException(e);
                    }
                } else {
                    productViewModel.addToCart(String.valueOf(productDetails.getQuantity()), productDetails.getId());
                }
            } else {
                productViewModel.addToCart(String.valueOf(productDetails.getQuantity()), productDetails.getId());
            }

        });

        return binding.getRoot();
    }

    /**
     * @return Handle API Response
     */
    private androidx.lifecycle.Observer<ResponseBody> responseBody() {
        return response -> {
            setProductImage();
            setUpRecyclerView();
            setUpQuantitySpinner();


            if (productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_VIEW)
                    && productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_ANR)) {
                try {
                    for (int j = 0; j < 20; j++) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    AppUtils.handleRumException(e);
                    e.printStackTrace();
                }
            } else if (productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_VIEW)
                    && productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_FREEZE)) {
                try {
                    for (int j = 0; j < 4; j++) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    AppUtils.handleRumException(e);
                    e.printStackTrace();
                }
            } else if (productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_VIEW)
                    && productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_4XX)) {
                eventViewModel.generateHttpNotFound();
            } else if (productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_VIEW)
                    && productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_5XX)) {
                eventViewModel.generateHttpError(productDetails.getId(),1);
            }

        };
    }

    /**
     * @return Handle Product add to cart response API Response
     */
    private androidx.lifecycle.Observer<ResponseBody> handleAddProductToCartResponse() {
        return response -> {
            if (getActivity() instanceof MainActivity) {
                Boolean isFromCart = ((MainActivity) getActivity()).getMainViewModel().getIsFromCart().getValue();
                if (isFromCart != null && isFromCart) {
                    ((MainActivity) getActivity()).getMainViewModel().getIsFromCart().setValue(Boolean.FALSE);
                } else {
                    if (productDetails.getErrorType().equalsIgnoreCase(AppConstant.ErrorType.ERR_CRASH)
                            && productDetails.getErrorAction().equalsIgnoreCase(AppConstant.ErrorAction.ACTION_CART)) {
                        throw new RuntimeException(getString(R.string.rum_event_app_crash));
                    }
                    AppUtils.storeProductInCart(getActivity(),productDetails);
                    if (getActivity() instanceof MainActivity
                            && ((MainActivity) getActivity()).getBottomNavigationView() != null) {
                        BadgeDrawable badge = ((MainActivity) getActivity()).getBottomNavigationView().getOrCreateBadge(R.id.navigation_cart);
                        badge.setVisible(true);
                        int totalQty = 0;
                        for (Product product : AppUtils.getProductsFromPref(getActivity()).getProducts()) {
                            totalQty += product.getQuantity();
                        }
                        badge.setNumber(totalQty);
                    }

                    Span workflow = SplunkRum.getInstance().startWorkflow(getString(R.string.rum_event_add_to_cart));
                    workflow.setStatus(StatusCode.OK, getString(R.string.rum_event_add_to_cart_msg));
                    workflow.end();

                    NavOptions navOptions = new NavOptions.Builder()
                            .setLaunchSingleTop(false)
                            .setEnterAnim(R.anim.slide_in_right)
                            .setExitAnim(R.anim.slide_out_left)
                            .setPopEnterAnim(R.anim.slide_in_right)
                            .setPopExitAnim(R.anim.slide_out_left)
                            //.setPopUpTo(NavHostFragment.findNavController(ProductDetailsFragment.this).getGraph().getStartDestination(), false)
                            .build();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstant.IntentKey.IS_FROM_PRODUCT_DETAIL, true);
                    NavHostFragment.findNavController(ProductDetailsFragment.this).navigate(R.id.navigation_cart, bundle, navOptions);

                    if (getActivity() instanceof MainActivity
                            && ((MainActivity) getActivity()).getBottomNavigationView() != null) {
                        ((MainActivity) getActivity()).getBottomNavigationView().getMenu().findItem(R.id.navigation_cart).setChecked(true);
                    }
                }
            }
        };
    }

    private void setProductImage() {
        if (AppUtils.getImage(mContext, productDetails.getPicture()) != 0) {
            Glide.with(mContext).load(AppUtils.getImage(mContext, productDetails.getPicture()))
                    .placeholder(R.drawable.no_image_place_holder).centerCrop().into(binding.productImg);
        }

    }

    private void setUpQuantitySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.quantity, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        binding.quantitySpinner.setAdapter(adapter);
        binding.spinnerArrow.setOnClickListener(view -> binding.quantitySpinner.performClick());
        productDetails.setQuantity(1);
        binding.quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object selectedItem = adapterView.getItemAtPosition(i);
                if (selectedItem != null && !StringHelper.isEmpty(selectedItem.toString())) {
                    productDetails.setQuantity(Integer.parseInt(selectedItem.toString()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpRecyclerView() {
        int spanCount = 2;
        if (AppUtils.is10InchTablet(getActivity())) {
            spanCount = 3;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        binding.recyclerView.setLayoutManager(gridLayoutManager);
        ProductListAdapter productListAdapter = new ProductListAdapter(getContext(), otherProductList, this);
        binding.recyclerView.setAdapter(productListAdapter);




        Span workflow = SplunkRum.getInstance().startWorkflow(String.format(getString(R.string.rum_event_product_viewed),productDetails.getName()));
        workflow.setAttribute(getString(R.string.rum_event_attribute_name), productDetails.getName());
        workflow.setStatus(StatusCode.OK, getString(R.string.rum_event_product_viewed_msg));
        workflow.end();

        binding.setProduct(productDetails);
        binding.executePendingBindings();
    }

    public List<Product> getProductList() {
        return productList;
    }

    public ProductViewModel getProductViewModel() {
        return productViewModel;
    }

    public Product getProductDetails() {
        return productDetails;
    }

    public EventViewModel getEventViewModel() {
        return eventViewModel;
    }
}