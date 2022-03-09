//
//  ValidationAndError.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import SwiftUI

class ValidationView: UIView {
    
    var normalMessage = ""
    var errorMessage = ""
    var messageLabel = UILabel()
    
    func setupInitialView(message: String, error: String, label: UILabel) {
        
        normalMessage = message
        errorMessage = error
        messageLabel = label
        showError(isError: false)
    }
    
    func showError(isError: Bool) {
        
        if isError {
            let errorColor = UIColor.rgb(red: 219, green: 60, blue: 38, alpha: 1)
            self.borderColorView = errorColor
            messageLabel.text = errorMessage
            messageLabel.textColor = errorColor
        }
        else {
            self.borderColorView = UIColor.rgb(red: 223, green: 232, blue: 247, alpha: 1)
            messageLabel.text = normalMessage
            messageLabel.textColor = UIColor.rgb(red: 0, green: 0, blue: 0, alpha: 1)
        }
    }
    
    func showErrorWithCustomMessage(message: String)
    {
        let errorColor = UIColor.rgb(red: 219, green: 60, blue: 38, alpha: 1)
        self.borderColorView = errorColor
        messageLabel.text = message
        messageLabel.textColor = errorColor
    }
    
    func showFocused() {
        self.borderColorView = UIColor.rgb(red: 37, green: 216, blue: 253, alpha: 1)
        messageLabel.text = normalMessage
        messageLabel.textColor = UIColor.rgb(red: 0, green: 0, blue: 0, alpha: 1)
    }
}

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
