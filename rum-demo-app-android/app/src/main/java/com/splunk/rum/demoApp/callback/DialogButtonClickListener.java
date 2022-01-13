package com.splunk.rum.demoApp.callback;

public interface DialogButtonClickListener {

	void onPositiveButtonClicked(int dialogIdentifier);
	void onNegativeButtonClicked(int dialogIdentifier);
}
