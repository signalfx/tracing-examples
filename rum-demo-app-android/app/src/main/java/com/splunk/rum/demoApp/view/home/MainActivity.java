package com.splunk.rum.demoApp.view.home;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;
import com.splunk.rum.demoApp.databinding.ActivityMainBinding;
import com.splunk.rum.demoApp.model.entity.response.Product;
import com.splunk.rum.demoApp.util.AlertDialogHelper;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.ResourceProvider;
import com.splunk.rum.demoApp.view.base.activity.BaseActivity;
import com.splunk.rum.demoApp.view.base.viewModel.ViewModelFactory;
import com.splunk.rum.demoApp.view.home.viewModel.MainViewModel;
import com.splunk.rum.demoApp.view.product.fragment.ProductListFragment;

public class MainActivity extends BaseActivity implements DialogButtonClickListener {
    private Context mContext;
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;

        // Initialize data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Configure ViewModel
        mainViewModel = new ViewModelProvider(this, new ViewModelFactory(new ResourceProvider(getResources()))).get(MainViewModel.class);
        mainViewModel.createView(this);

        // Setup Toolbar
        setupToolbar();

        // Setup Navigation Component
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        setBottomNavigationView(binding.navView);


        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_right)
                .setPopExitAnim(R.anim.slide_out_left)
                //.setPopUpTo(navController.getGraph().getStartDestination(), false)
                .build();

        binding.navView.setOnItemSelectedListener(item -> {
            if (binding.navView.getSelectedItemId() != item.getItemId()) {
                navController.navigate(item.getItemId(), null, navOptions);
                return true;
            }
            return false;
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(binding.navView.getMenu())
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        setUpBadge();

    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    /**
     * Using this method total number of product quantity visible on cart icon in red badge
     */
    private void setUpBadge() {
        if (!CollectionUtils.isEmpty(AppUtils.getProductsFromPref(this).getProducts())) {
            BadgeDrawable badge = getBottomNavigationView().getOrCreateBadge(R.id.navigation_cart);
            badge.clearNumber();
            badge.setVisible(false);
            int totalQty = 0;
            for (Product product : AppUtils.getProductsFromPref(this).getProducts()) {
                totalQty += product.getQuantity();
            }
            badge.setVisible(true);
            badge.setNumber(totalQty);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getVisibleFragment();
        if (fragment instanceof ProductListFragment) {
            AlertDialogHelper.showDialog(mContext, "", getString(R.string.exit_msg), getString(R.string.yes), getString(R.string.no), false, this, AppConstant.DialogIdentifier.EXIT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPositiveButtonClicked(int dialogIdentifier) {
        if (dialogIdentifier == AppConstant.DialogIdentifier.EXIT) {
            finish();
        }
    }

    @Override
    public void onNegativeButtonClicked(int dialogIdentifier) {
    }


    /**
     * @return This method return list of fragment
     */
    public Fragment getVisibleFragment() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    public MainViewModel getMainViewModel() {
        return mainViewModel;
    }
}