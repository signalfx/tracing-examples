//
//  File.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import Foundation
import ObjectMapper
import SplunkOtel
import OpenTelemetrySdk
import OpenTelemetryApi



class DataService{
    
    // MARK: - Singleton
    static let shared = DataService()
    
    // MARK: - Generic Request
    public static func request<T: Mappable>(_ urlString: String,
                                            method: String,
                                            params: [String : Any]?,
                                            shouldDisplayLoader : Bool = true,
                                            type: T.Type,
                                            completion: @escaping (T?, String?,Int) -> Void){
        
        handleNoInternetConnection {
            guard let url = URL.init(string: urlString) else {return}
            var request = URLRequest.init(url: url)
            request.timeoutInterval = 60.0
            //        request.setValue("keep-alive", forHTTPHeaderField: "Connection")
            request.httpMethod = method
            
            if let bodyParameters = params {    
                let postString = getPostString(params: bodyParameters)
                request.httpBody = postString.data(using: .utf8)
            }
            
            let session = URLSession.init(configuration: .default)
            
            if shouldDisplayLoader {
                APProgressHUD.shared.showProgressHUD(nil)
            }
            session.dataTask(with: request, completionHandler: { dataOfResponse, responseofAPI, errorOfAPI in
                
                if shouldDisplayLoader {                
                    APProgressHUD.shared.dismissProgressHUD()
                }
                if let error = errorOfAPI {
                    completion(nil, error.localizedDescription , 0)
                } else if (responseofAPI as? HTTPURLResponse)?.statusCode != 200 {
                    completion(nil, "Failed to get 200 Response code from API", (responseofAPI as? HTTPURLResponse)?.statusCode ?? 0)
                } else {
                    completion(nil, nil , 200)
                }
            }).resume()
        }
    }
    
    
    fileprivate static func getPostString(params:[String:Any]) -> String
    {
        var data = [String]()
        for(key, value) in params
        {
            data.append(key + "=\(value)")
        }
        return data.map { String($0) }.joined(separator: "&")
    }
}

enum ApiName: String{
    case ProductList = ""
    case AddToCart = "AddToCart"
    case CheckOut = "cart/checkout"
    case GeneratePayment = "payment/send"
    case ProductDetails = "product/"
    case Cart = "cart"
    case GenerateCartEmpty = "cart/clear"
    case GenerateSalesTax = "checkout/calculateTax"
}
func getURL(for apiname: String)-> String{
    return "\(AppVariables.current.configURL)\(apiname)"
}


