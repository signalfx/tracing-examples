//
//  CheckOutVM.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation

class CheckOutVM {
    
    /**
     *description: Checkout API call.
     *Parameter email: Email of user placing an order
     *Parameter streetAddress: Street Address of user placing an order
     *Parameter zipCode: Zip Code of user placing an order
     *Parameter city: City of user placing an order
     *Parameter state: State of user placing an order
     *Parameter country: Country of user placing an order
     *Parameter creditCardNumber: Credti Card Number of user placing an order
     *Parameter creditCardExpMonth: Credit Card Expiry Month of user placing an order
     *Parameter creditCardExpYear: Credit Card Expiry Year of user placing an order
     *Parameter creditCardCVV: Credit Card CVV of user placing an order
     *Parameter completion: Completion handler for call back when API is called and giving response.
    */
    func callCheckoutAPI(email: String, streetAddress: String, zipCode: String, city: String, state: String, country: String, creditCarNumber: String, creditCardExpMonth: String, creditCardExpYear: String, creditCardCVV: String, completion: @escaping (_ errorMessage : String?)->Void) {
        
        let parameters : [String : Any] = ["email" : email,
                                       "street_address" : streetAddress,
                                       "zip_code" : zipCode,
                                       "city": city,
                                       "state": state,
                                       "country" : country,
                                       "credit_card_number": creditCarNumber,
                                       "credit_card_expiration_month" : creditCardExpMonth,
                                       "credit_card_expiration_year" : creditCardExpYear,
                                       "credit_card_cvv" : creditCardCVV]
        
        DataService.request(getURL(for: ApiName.CheckOut.rawValue), method: "POST", params: parameters, type: ProductDetail.self) { productDetail, errorMessage, responseCode in
            completion(errorMessage)
        }
    }
}
