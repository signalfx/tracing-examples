//
//  StringExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit

extension String
{
    
    func localized(bundle: Bundle = .main, tableName: String = "Localization") -> String {
        
        return NSLocalizedString(self, tableName: tableName, value: "\(self)", comment: "")
        
    }
    
    func validateEmail() -> Bool {
        let emailRegEx = "^(([\\w-+]+\\.)+[\\w-+.]+|([a-zA-Z]{1}|[\\w-+]{2,64}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]? [0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]? [0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z0-9](?:[a-zA-Z0-9-]{0,1})+\\.)+[a-zA-Z]{1}[a-zA-Z0-9-]{1,23})$"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: self)
    }
    //MARK: - URL is valid or not
    /*
     Below code is reference from CruzAlex on Stackoverflow.
     Reference: https://stackoverflow.com/a/51259604
     */
    func isValidUrl() -> Bool {
        
        let urlRegEx = "((https|http)://)((\\w|-)+)(([.]|[/])((\\w|-)+))+"//"((?:http|https)://)?(?:www\\.)?[\\w\\d\\-_]+\\.\\w{2,3}(\\.\\w{2})?(/(?<=/)(?:[\\w\\d\\-./_]+)?)?"
        return NSPredicate(format: "SELF MATCHES %@", urlRegEx).evaluate(with: self)
    }
    
}

func randomString(length: Int, onlyNumbers : Bool = false) -> String {
    var letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    if onlyNumbers {
        letters = "0123456789"
    }
    return String((0..<length).map{ _ in letters.randomElement()! })
}
