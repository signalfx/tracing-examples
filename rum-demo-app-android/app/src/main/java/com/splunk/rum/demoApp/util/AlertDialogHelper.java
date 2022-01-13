package com.splunk.rum.demoApp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import com.splunk.rum.demoApp.R;
import com.splunk.rum.demoApp.callback.DialogButtonClickListener;


@SuppressWarnings({"ALL", "unused"})
public final class AlertDialogHelper {

    public static void showDialog(Context context,
                                  String dialogTitle, String dialogMessage, String textPositiveButton,
                                  String textNegativeButton, boolean isCancelable,
                                  final DialogButtonClickListener buttonClickListener,
                                  final int dialogIdentifier) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (!StringHelper.isEmpty(dialogTitle)) {
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

    public static void showDialog(Context context, String dialogMessage) {
        showDialog(context, null, dialogMessage, context.getString(R.string.ok), null, true, null, 0);
    }

    public static void showDialog(Context context, String title, String dialogMessage, final DialogButtonClickListener buttonClickListener) {
        showDialog(context, title, dialogMessage, context.getString(R.string.ok), null, true, buttonClickListener, 0);
    }

}
