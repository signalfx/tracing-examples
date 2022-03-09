//
//  UIDeviceExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 12/01/22.
//

import Foundation
import UIKit

extension UIDevice{
    var hasNotch: Bool {
            let bottom = UIApplication.shared.keyWindow?.safeAreaInsets.bottom ?? 0
            return bottom > 0
        }
    
}
