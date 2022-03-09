//
//  Order.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import ObjectMapper


class Order: Mappable {
    var productID: Int = 0
    var price: Int = 0
    var productName: String = ""
    var url: String = ""
    var thumbnailURL: String = ""
    var currency: String = ""

    required init?(map: Map) {
    }

    func mapping(map: Map) {
        productID       <- map["productID"]
        price           <- map["price"]
        productName     <- map["productName"]
        url             <- map["url"]
        thumbnailURL    <- map["thumbnailURL"]
        currency        <- map["currency"]
    }
}
