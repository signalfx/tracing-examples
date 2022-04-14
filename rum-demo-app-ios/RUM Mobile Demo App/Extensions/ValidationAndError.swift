//
//  ValidationAndError.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import SwiftUI


enum CustomError : Error {
    case notFound
    case incorrectPassword
    case unexpected(code:Int)

}
extension CustomError : CustomStringConvertible{
    public var description: String {
        switch self {
        case .notFound:
            return "File is not exist at path"
        case .incorrectPassword:
            return "Provided password is not corrent"
        case .unexpected(_):
            return "Unexpected error occur"
        }
    }
    
}

enum txtValidation {
    case email
    case streetAddress
    case zipcode
    case country
    case state
    case city
    case ccNumber
    case month
    case year
    case cvv
}
