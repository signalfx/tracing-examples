//
//  BaseRequestData.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit

class BaseRequestData {
    public var param1 = ""
    var param2 : Int!
    
    init()
    {
        param1 = ""
        param2 = 2
    }
    
    func toDictionary() -> NSDictionary
    {
        let requestDic = NSMutableDictionary()
        
        let strClientRequest = Date().toString()
        requestDic.setValue(strClientRequest, forKey: "param1")
        requestDic.setValue(2, forKey: "param2")
        
        return requestDic
    }
    
    func getURL() -> String
    {
        return ""
    }
}

