//
//  BaseResponseData.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import Foundation
import ObjectMapper


struct BaseResponseData<T:Mappable>: Mappable {
    public var item: T?
    public var isSuccessful:Bool?
    public var error: String?
    init?(map: Map){
    }
    mutating func mapping(map: Map) {
        item            <- map["item"]
        isSuccessful    <- map["IsSuccessfull"]
        error           <- map["error"]
    }
}

