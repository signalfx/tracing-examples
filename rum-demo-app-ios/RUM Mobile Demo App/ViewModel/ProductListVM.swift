//
//  ProductListVM.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import ObjectMapper

class ProductListVM {
    
    
    // MARK: - Properties
    var products: ProductList?
    
    var error: Error? {
        didSet { self.showAlertClosure?() }
    }
    
    // MARK: - Closures for callback, since we are not using the ViewModel to the View.
    var showAlertClosure: (() -> ())?
    var updateLoadingStatus: (() -> ())?
    var didFinishFetch: (() -> ())?
    
    // MARK: - Network call
    /**
     *description: API call for the product list API..
    */
    func fetchProducts(completion: @escaping (_ errorMessage : String?, _ products : [ProductList])->Void) {
       DataService.request( getURL(for: ApiName.ProductList.rawValue) , method: "GET", params: nil, type: ProductList.self) { (productlist, errorMessage, responsecode) in
                self.products = productlist
                
           if let error = errorMessage {
               completion(error, [])
               return
           }
           
           guard let jsonFilePath = Bundle.main.path(forResource: "products", ofType: "json") else {
               completion(nil,[])
               return
           }
           
           
           guard let jsonData = try? Data.init(contentsOf: URL.init(fileURLWithPath: jsonFilePath)) else {
               completion(nil,[])
               return
           }
           
           guard let json = try? JSONSerialization.jsonObject(with: jsonData, options: .mutableContainers) as? [String : Any] else {
               completion(nil,[])
               return
           }
         
           guard let products = Mapper<ProductList>().mapArray(JSONObject: json["products"] ) else {
               completion(nil,[])
               return
           }
           
           completion(nil, products)
        }
    }
    
}
