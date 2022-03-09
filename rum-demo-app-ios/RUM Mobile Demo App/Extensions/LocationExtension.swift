//
//  LocationExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 10/02/22.
//

import Foundation
import CoreLocation

extension CLLocation {
    func fetchCountry(completion: @escaping (_ country:  String?, _ error: Error?) -> ()) {
        CLGeocoder().reverseGeocodeLocation(self) { completion($0?.first?.country, $1) }
    }
}
