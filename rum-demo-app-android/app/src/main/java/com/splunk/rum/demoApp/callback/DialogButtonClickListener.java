package com.splunk.rum.demoApp.callback;

public interface DialogButtonClickListener {


	/**
	 * @param dialogIdentifier Alert button positive button identifier for multiple dialog in single activity
	 */
	void onPositiveButtonClicked(int dialogIdentifier);


	/**
	 * @param dialogIdentifier Alert button negative button identifier for multiple dialog in single activity
	 */
	void onNegativeButtonClicked(int dialogIdentifier);
}
