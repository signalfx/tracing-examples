//
//  Reachability.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 07/02/22.
//

import Foundation
import SystemConfiguration
import UIKit

public class Reachability {

    class func isConnectedToNetwork() -> Bool {

        var zeroAddress = sockaddr_in(sin_len: 0, sin_family: 0, sin_port: 0, sin_addr: in_addr(s_addr: 0), sin_zero: (0, 0, 0, 0, 0, 0, 0, 0))
        zeroAddress.sin_len = UInt8(MemoryLayout.size(ofValue: zeroAddress))
        zeroAddress.sin_family = sa_family_t(AF_INET)

        let defaultRouteReachability = withUnsafePointer(to: &zeroAddress) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {zeroSockAddress in
                SCNetworkReachabilityCreateWithAddress(nil, zeroSockAddress)
            }
        }

        var flags: SCNetworkReachabilityFlags = SCNetworkReachabilityFlags(rawValue: 0)
        if SCNetworkReachabilityGetFlags(defaultRouteReachability!, &flags) == false {
            return false
        }

        // Working for Cellular and WIFI
        let isReachable = (flags.rawValue & UInt32(kSCNetworkFlagsReachable)) != 0
        let needsConnection = (flags.rawValue & UInt32(kSCNetworkFlagsConnectionRequired)) != 0
        let ret = (isReachable && !needsConnection)

        return ret

    }
}

func handleNoInternetConnection(_ internetCompletion : @escaping ()->Void, _ okAction: (()->Void)? = nil) {
    if !Reachability.isConnectedToNetwork() {
        let appDel = UIApplication.shared.delegate as? AppDelegate
        
        if let rootVC = appDel?.window?.rootViewController {

            rootVC.showAlertNativeDoubleAction(StringConstants.noInternetTitle, message: StringConstants.noInternetMessage, buttonTitle1: "Retry", clickHandler1: {
                if Reachability.isConnectedToNetwork() {
                    internetCompletion()
                } else {
                    handleNoInternetConnection(internetCompletion, okAction)
                }
            }, buttonTitle2: "Okay", clickHandler2: {
                okAction?()
            }, dismissCompletion: nil)
        }
        return
    } else {
        internetCompletion()
    }
}
