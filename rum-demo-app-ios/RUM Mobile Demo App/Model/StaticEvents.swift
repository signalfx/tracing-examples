//
//  StaticEvents.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 17/01/22.
//

import UIKit
import Foundation
import ObjectMapper


class StaticEvents: Mappable {
    var kind: String = ""
    var localId: String = ""
    var email: String = ""
    var displayName: String = ""
    var idToken: String = ""
    var registered: Bool = false
    var refreshToken: String = ""
    var expiresIn: String = ""
  

    required init?(map: Map) {
    }

    func mapping(map: Map) {
        kind                     <- map["kind"]
        localId                  <- map["localId"]
        email                    <- map["email"]
        displayName              <- map["displayName"]
        idToken                  <- map["idToken"]
        registered               <- map["registered"]
        refreshToken             <- map["refreshToken"]
        expiresIn                <- map["expiresIn"]
        
    }
}
