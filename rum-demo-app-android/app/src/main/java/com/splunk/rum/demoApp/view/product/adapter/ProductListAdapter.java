package com.splunk.rum.demoApp.view.product.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.RowProductListBinding;
import com.splunk.rum.demoApp.model.entity.response.NewProduct;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;
import com.splunk.rum.demoApp.view.product.fragment.ProductDetailsFragment;
import com.splunk.rum.demoApp.view.product.fragment.ProductListFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<NewProduct> productList;
    private final Fragment fragment;

    public ProductListAdapter(Context context, List<NewProduct> productList, Fragment fragment) {
        this.mContext = context;
        this.productList = new ArrayList<>();
        this.productList.addAll(productList);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_product_list, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final NewProduct product = productList.get(position);
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        loadProductImage(itemViewHolder.getBinding().productImage, product.getPicture());
        itemViewHolder.getData(product);
        itemViewHolder.getBinding().parentLayout.setOnClickListener(view -> {
            Parcelable parcelableProduct = Parcels.wrap(product);
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppConstant.IntentKey.PRODUCT_DETAILS, parcelableProduct);

            if (fragment instanceof ProductDetailsFragment) {
                Parcelable parcelableProductArray = Parcels.wrap(((ProductDetailsFragment) this.fragment).getProductList());
                bundle.putParcelable(AppConstant.IntentKey.PRODUCT_ARRAY,parcelableProductArray);
                bundle.putBoolean(AppConstant.IntentKey.IS_FROM_PRODUCT_ITEM,true);
                NavHostFragment.findNavController(fragment).navigate(R.id.action_navigation_product_detail_self, bundle);
            } else {
                Parcelable parcelableProductArray = Parcels.wrap(((ProductListFragment) this.fragment).getProductList());
                bundle.putParcelable(AppConstant.IntentKey.PRODUCT_ARRAY,parcelableProductArray);
                bundle.putBoolean(AppConstant.IntentKey.IS_FROM_PRODUCT_ITEM,true);
                NavHostFragment.findNavController(fragment).navigate(R.id.action_navigation_home_to_productDetailsFragment, bundle);
            }
        });
    }

    private void loadProductImage(ImageView imageView, String imageName) {
        if (!StringHelper.isEmpty(imageName) && AppUtils.getImage(mContext,imageName) != 0) {
            Glide.with(mContext).load(AppUtils.getImage(mContext,imageName))
                    .placeholder(R.drawable.no_image_place_holder).centerCrop().into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final RowProductListBinding binding;

        public void getData(NewProduct product) {
            binding.setProduct(product);
        }

        public ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public RowProductListBinding getBinding() {
            return binding;
        }
    }
}