//
//  DateExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit

extension Date
{
    func toString() -> String
    {
        return self.toString(dateFormat: "yyyy-MM-dd H:m:ss.SSSS")
    }
    
    func toString(dateFormat: String) -> String
    {
        let df = DateFormatter()
        df.dateFormat = dateFormat
        return df.string(from: self)
    }
    
    func gerDiffInSeconds(start: Date) -> Int  {
        let calendar = Calendar.current
        let dateComponents = calendar.dateComponents([Calendar.Component.second], from: start, to: self)
        if let seconds = dateComponents.second {
            return Int(seconds)
        }
        return 0
    }
    
    func getFormattedDate(style: DateFormatter.Style) -> String
    {
       let dateFormatter = DateFormatter()
       dateFormatter.dateStyle = style
       return dateFormatter.string(from: self)
    }
    
    func getFormattedTime(style: DateFormatter.Style) -> String
    {
        let dateFormatter = DateFormatter()
        dateFormatter.timeStyle = style
        return dateFormatter.string(from: self)
    }
}
