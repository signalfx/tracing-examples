package com.splunk.rum.demoApp.view.product.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.splunk.rum.demoApp.R;

import java.util.List;

public class QuantitySpinnerAdapter extends ArrayAdapter<String> {

    LayoutInflater flater;

    @SuppressWarnings("ALL")
    public QuantitySpinnerAdapter(Activity context, int resouceId, int textviewId, List<String> list) {
        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position,parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return rowview(convertView, position,parent);
    }

    private View rowview(View convertView, int position,@NonNull ViewGroup parent) {

        String rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spinner_item, parent, false);

            holder.txtTitle = rowview.findViewById(R.id.tvSpinnerItem);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }
        holder.txtTitle.setText(rowItem);
        holder.txtTitle.setContentDescription(String.format(getContext().getString(R.string.product_detail_quantity_),rowItem));

        return rowview;
    }

    @SuppressWarnings("ALL")
    private static class viewHolder {
        TextView txtTitle;
    }
}