package com.splunk.rum.demoApp.view.cart.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.CollectionUtils;
import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.databinding.RowProductCartListBinding;
import com.splunk.rum.demoApp.model.entity.response.Product;
import com.splunk.rum.demoApp.util.AppConstant;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Product> productList = new ArrayList<>();

    public CartProductListAdapter(Context context, ArrayList<Product> productList) {
        this.mContext = context;
        this.productList.addAll(productList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_product_cart_list, viewGroup, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Product product = this.productList.get(position);

        double price = product.getPriceUsd().getPrice() * product.getQuantity();
        String formattedPrice = new DecimalFormat("##.##").format(price);
        product.getPriceUsd().setPrice(Double.parseDouble(formattedPrice));

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        loadProductImage(itemViewHolder.getBinding().productImage, product.getPicture());
        itemViewHolder.getData(product);
        itemViewHolder.getBinding().parentLayout.setOnClickListener(view -> {

            Bundle bundle = new Bundle();
            Parcelable parcelableProduct = Parcels.wrap(product);
            bundle.putParcelable(AppConstant.IntentKey.PRODUCT_DETAILS, parcelableProduct);
        });
    }

    private void loadProductImage(ImageView imageView, String imageName) {
        if (StringHelper.isNotEmpty(imageName) && AppUtils.getImage(mContext,imageName) != 0) {
            Glide.with(mContext).load(AppUtils.getImage(mContext,imageName))
                    .placeholder(R.drawable.no_image_place_holder).centerCrop().into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private final RowProductCartListBinding binding;

        public void getData(Product product) {
            binding.setProduct(product);
        }

        public ItemViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public RowProductCartListBinding getBinding() {
            return binding;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if (!CollectionUtils.isEmpty(productList)) {
            productList.clear();
            notifyDataSetChanged();
        }
    }
}