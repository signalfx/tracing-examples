package com.splunk.rum.demoApp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;


public final class AlertDialogHelper {

    /**
     * @param context View context
     * @param dialogTitle Dialog title ==> example "Alert" , If you don't want to show title then pass null
     * @param dialogMessage* Dialog message ==> example "Are you sure you want remove this item?"
     * @param textPositiveButton* Dialog positive button ==> example "Yes"
     * @param textNegativeButton Dialog negative button ==> example "No" If you don't want to add this button then pass null
     * @param isCancelable Dilog cancelable when user click outside ==> true/false
     * @param buttonClickListener interface implentation in activity or fragment then pass this if you don't want click listener then pass null
     * @param dialogIdentifier dialogIdentifier pass integer use app constant file example ==> 1,2,3 if you have only one dialog then pass 0
     */
    public static void showDialog(Context context,
                                  String dialogTitle, String dialogMessage, String textPositiveButton,
                                  String textNegativeButton, boolean isCancelable,
                                  final DialogButtonClickListener buttonClickListener,
                                  final int dialogIdentifier) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (StringHelper.isNotEmpty(dialogTitle)) {
            alertDialogBuilder.setTitle(dialogTitle);
        }
        alertDialogBuilder.setMessage(dialogMessage);
        alertDialogBuilder.setCancelable(isCancelable);


        alertDialogBuilder.setPositiveButton(textPositiveButton, (dialog, id1) -> {
            if (buttonClickListener == null) {
                dialog.dismiss();
            } else {
                buttonClickListener.onPositiveButtonClicked(dialogIdentifier);
            }
        });

        if (textNegativeButton != null) {
            alertDialogBuilder.setNegativeButton(textNegativeButton, (dialog, id1) -> {
                if (buttonClickListener == null) {
                    dialog.dismiss();
                } else {
                    buttonClickListener.onNegativeButtonClicked(dialogIdentifier);
                }
            });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        //	alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;

        if (!((Activity) context).isFinishing()) {
            alertDialog.show();
        }
    }

    /**
     * @param context View context
     * @param dialogMessage Dialog message
     */
    @SuppressWarnings("unused")
    public static void showDialog(Context context, String dialogMessage) {
        showDialog(context, null, dialogMessage, context.getString(R.string.ok), null, true, null, 0);
    }

    /**
     * @param context View context
     * @param title Dialog title ==> example "Alert" , If you don't want to show title then pass null
     * @param dialogMessage Dialog message ==> example "Are you sure you want remove this item?"
     * @param buttonClickListener buttonClickListener interface implentation in activity or fragment then pass this. If you don't want click listener then pass null
     */
    @SuppressWarnings("unused")
    public static void showDialog(Context context, String title, String dialogMessage, final DialogButtonClickListener buttonClickListener) {
        showDialog(context, title, dialogMessage, context.getString(R.string.ok), null, true, buttonClickListener, 0);
    }

}
