//
//  ProductDetailVM.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation

class ProductDetailVM {
    
    /**
     *description: API call for the product details API.
     *Parameter productID: The ID of product selected by user.
    */
    func fetchProductDetails(productID: String, completion: @escaping ()->Void) {
        
        DataService.request(getURL(for: "\(ApiName.ProductDetails.rawValue)\(productID)"), method: "GET", params: nil, type: ProductDetail.self) { productDetail, errorMessage, responseCode in
            
            completion()
        }
    }
}
