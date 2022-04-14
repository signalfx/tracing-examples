//
//  StringConstants.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation

struct StringConstants{
    
    //MARK : - alert messages
    static let alertTitle  = "RUM Demo".localized()
    static let noURLMsg = "Please enter URL.".localized()
    static let urlIsNotProperMsg = "Entered URL is not valid. Please try again with valid URL.".localized()
    static let paymentFailed = "Payment Failed"
    static let paymentFailedDueToCC = "The provided credit card number is invalid, resulting in payment failure."
    static let paymentFailedDueToLocation = "We are apologise for inconvenience, but we cannot accept Payment in France."
    static let noInternetMessage = "There was a problem connecting to the server, please checked your network connection, and then try again."
    static let noInternetTitle = "Network issue"
    static let confirmEmptyCart = "Are you sure you want to empty your cart?"
    static let restartAppToApplyConfigChanges = "You need to restart your application to reflect the changes made in configuration"
}
