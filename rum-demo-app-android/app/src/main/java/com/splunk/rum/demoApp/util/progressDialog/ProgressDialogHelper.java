package com.splunk.rum.demoApp.util.progressDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.util.AppUtils;
import com.splunk.rum.demoApp.util.StringHelper;

public class ProgressDialogHelper {

    private Dialog roundedProgressDialog;
    private final Context context;
    private Dialog progressDialog;

    public ProgressDialogHelper(Context context) {
        this.context = context;
    }

    public void showProgressDialog(String message) {
        try {
            if (context == null) return;

            if (progressDialog == null) {
                progressDialog = getProgressDialog(context, message);
            }

            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing() &&
                        !progressDialog.isShowing()) {
                    progressDialog.show();
                }
            } else if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } catch (Exception e) {
            AppUtils.handleRumException(e);
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog == null || !progressDialog.isShowing()) return;

            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing()) {
                    progressDialog.dismiss();
                }
            } else {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            AppUtils.handleRumException(e);
        }
    }

    private Dialog getProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.customAlertDialogParentThemeStyle);
        progressDialog.setCancelable(false);
        if (!StringHelper.isEmpty(message)) {
            progressDialog.setMessage(message);
        }
        return progressDialog;
    }

    public void showCircularProgressDialog() {
        try {
            if (context == null) return;

            roundedProgressDialog = setDialog(context);

            if (context instanceof Activity) {
                Activity activity = ((Activity) context);
                if (!activity.isFinishing() && roundedProgressDialog != null
                        && !roundedProgressDialog.isShowing()) {
                    roundedProgressDialog.show();
                }
            } else {
                if (roundedProgressDialog != null
                        && !roundedProgressDialog.isShowing()) {
                    roundedProgressDialog.show();
                }
            }
        }catch (Exception e){
            AppUtils.handleRumException(e);
        }

    }

    public void hideCircularProgressDialog() {
        try {
            if (roundedProgressDialog == null || !roundedProgressDialog.isShowing()) return;

            if ((roundedProgressDialog != null)
                    && roundedProgressDialog.isShowing()) {
                roundedProgressDialog.dismiss();
            }
        } catch (Exception e) {
            AppUtils.handleRumException(e);
        }finally {
            roundedProgressDialog = null;
        }
    }


    private Dialog setDialog(Context context) {
        try {
            if(context == null) return null;

            Dialog dialog = new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = mInflater.inflate(R.layout.dialog_loading, null, false);
            ProgressWheel wheel = view.findViewById(R.id.progress_wheel);
            wheel.setBarColor(getThemeColors(context, R.attr.colorAccent));
            dialog.setContentView(view);
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            return dialog;
        }catch (Exception e){
            AppUtils.handleRumException(e);
        }
        return null;
    }

    private int getThemeColors(final Context context, int color) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(color, value, true);
        return value.data;
    }
}
