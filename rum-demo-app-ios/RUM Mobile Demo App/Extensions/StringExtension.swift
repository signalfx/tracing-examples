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
    
   
    var isPhoneNumber: Bool {
        do {
            let detector = try NSDataDetector(types: NSTextCheckingResult.CheckingType.phoneNumber.rawValue)
            let matches = detector.matches(in: self, options: [], range: NSMakeRange(0, self.count))
            if let res = matches.first {
                return res.resultType == .phoneNumber && res.range.location == 0 && res.range.length == self.count
            } else {
                return false
            }
        } catch {
            return false
        }
    }
    
    func validateEmail() -> Bool {        
        let emailRegEx = "^(([\\w-+]+\\.)+[\\w-+.]+|([a-zA-Z]{1}|[\\w-+]{2,64}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]? [0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]? [0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z0-9](?:[a-zA-Z0-9-]{0,1})+\\.)+[a-zA-Z]{1}[a-zA-Z0-9-]{1,23})$"
        
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailTest.evaluate(with: self)
    }
    //MARK: - URL is valid or not
    func isValidUrl() -> Bool {
              
        let urlRegEx = "((https|http)://)((\\w|-)+)(([.]|[/])((\\w|-)+))+"//"((?:http|https)://)?(?:www\\.)?[\\w\\d\\-_]+\\.\\w{2,3}(\\.\\w{2})?(/(?<=/)(?:[\\w\\d\\-./_]+)?)?"
        return NSPredicate(format: "SELF MATCHES %@", urlRegEx).evaluate(with: self)
}

    
    func replacingMutipleCharcters(of strings:[String], with replacement:String) -> String
    {
        var newString = self
        for string in strings {
            newString = newString.replacingOccurrences(of: string, with: replacement, options: .caseInsensitive)
        }
        return newString
    }
    
    func index(from: Int) -> Index {
        return self.index(startIndex, offsetBy: from)
    }

    func substring(from: Int) -> String {
        let fromIndex = index(from: from)
        return String(self[fromIndex...])
    }

    func substring(to: Int) -> String {
        let toIndex = index(from: to)
        return String(self[..<toIndex])
    }

    func substring(with r: Range<Int>) -> String {
        let startIndex = index(from: r.lowerBound)
        let endIndex = index(from: r.upperBound)
        return String(self[startIndex..<endIndex])
    }
}

func randomString(length: Int, onlyNumbers : Bool = false) -> String {
    var letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    if onlyNumbers {
        letters = "0123456789"
    }
  return String((0..<length).map{ _ in letters.randomElement()! })
}
