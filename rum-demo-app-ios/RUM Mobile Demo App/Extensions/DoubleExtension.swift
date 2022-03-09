//
//  DoubleExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 14/02/22.
//

import Foundation

extension Double {
    /// Rounds the double to decimal places value
    func rounded(toPlaces places:Int) -> Double {
        let divisor = pow(10.0, Double(places))
        return (self * divisor).rounded() / divisor
    }
}
