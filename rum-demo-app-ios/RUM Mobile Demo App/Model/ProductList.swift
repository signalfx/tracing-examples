//
//  ProductList.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import Foundation
import ObjectMapper


enum ProductErrorType : String {
    case crash = "crash"
    case freeze = "freeze"
    case error4xx = "4xx"
    case error5xx = "5xx"
    case exception = "exception"
}

enum ProductErrorAction : String {
    case cart = "cart"
}

class ProductList: Mappable {
    var id : String = ""
    var description: String = ""
    var picture: String = ""
    var name : String = ""
    var priceUsd : productPrice?
    var categories : [String] = []
    var errorType : String = ""
    var errorAction : String = ""
    var availableQty = 1
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        picture         <- map["picture"]
        name            <- map["name"]
        priceUsd        <- map["priceUsd"]
        id              <- map["id"]
        description     <- map["description"]
        categories      <- map["categories"]
        errorType       <- map["errorType"]
        errorAction     <- map["errorAction"]
        availableQty    <- map["availableQty"]
    }
}

class productPrice : Mappable {
    
    var currencyCode : String?
    var units : Int?
    var nanos : Int?
    var price : Double?
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        currencyCode    <- map["currencyCode"]
        units           <- map["units"]
        nanos           <- map["nanos"]
        price           <- map["price"]
    }
}


