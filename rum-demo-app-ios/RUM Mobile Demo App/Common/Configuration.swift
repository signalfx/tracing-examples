//
//  Configuration.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import Foundation

class Configuration : NSObject{
    
    let timeOutPeriod = 4 // Minutes
    
    //MARK: - Environment
    let rumEnvironmentName = "ENTER_YOUR_RUM_ENVIRONMENT_NAME"
    
    // MARK: - Splunk RUM BeaconURL
    let beaconURL = "ENTER_YOUR_RUM_AUTH_TOKEN"
    
    // MARK: - Splunk RUMAuth key
    let rumAuth = "ENTER_YOUR_RUM_AUTH_TOKEN"
    
    // MARK: - Splunk tracer name
    let RUM_TRACER_NAME = "SplunkRum"
    
    //MARK: - RUM URL
    var rumURL = "ENTER_YOUR_RUM_URL"
    
    //MARK: - Realm
    let realmValue = ""//"ENTER_DEFAULT_REALM"
    
    //MARK: - Application Name
    let appName = "Rum Mobile Demo App"
    
    // MARK: - root URL
    var rootAPIUrl : String {
        get {
            
            if let baseURL = AppDefaults.getValue(for: .appBaseURL) as? String, !baseURL.isEmpty {
                return baseURL
            }
            return "ENTER_YOUR_ROOT_API_URL"
        }
        
        set {
            AppDefaults.setValue(newValue, for: .appBaseURL)
        }
    }
    
    //MARK: - RUM URL
    //    var rumURL = "https://app.us1.signalfx.com"//"ENTER_YOUR_RUM_URL"
    
    // MARK: - Font Scheme
    let fontNameLight = "Roboto-Light"
    let fontNameThin = "Roboto-Thin"
    let fontNameRegular = "Roboto-Regular"
    let fontNameMedium = "Roboto-Medium"
    let fontNameBold = "Roboto-Bold"
    let fontNameBlack = "Roboto-Black"
    
    // MARK: -  Font Sizes
    let largeHeaderFontSize = CGFloat(35)
    let largeWelcomeFontSize = CGFloat(35)
    let largePassCodeFontSize = CGFloat(35)
    let cvvFontSize = CGFloat(30)
    let headerTitleFontSize = CGFloat(26)
    let largeTitleFontSize = CGFloat(24)
    let mainTitleFontSize = CGFloat(24)
    let subTitleFontSize = CGFloat(22)
    let subTitleMidFontSize = CGFloat(21)
    let largeLabelSize = CGFloat(18)
    let mediumLabelSize = CGFloat(16)
    let midLargeLabelSize = CGFloat(14)
    let smallLabelSize = CGFloat(12)
    let topMenuFontSize = CGFloat(11)
    let verySmallLabelSize = CGFloat(10)
    let largeTextSize = CGFloat(56)
    
    
    // MARK: - Fonts / Text Colors
    let commonScreenBgColor = UIColor.rgb(red: 23, green: 162, blue: 184, alpha: 1)
    let disableButtonBgColor = UIColor.rgb(red: 210, green: 210, blue: 210, alpha: 1)
    let textGrayActive = UIColor.rgb(red: 73, green: 73, blue: 72, alpha: 1)
    let textGrayInactive = UIColor.rgb(red: 73, green: 73, blue: 72, alpha: 0.1)
    let textGreyBackground = UIColor.rgb(red: 246, green: 246, blue: 246, alpha: 1)
    let accentColor = UIColor.rgb(red: 29, green: 172, blue: 251, alpha: 1)
    let secondaryAccentColor = UIColor.white
    let htmlAccentColor = "rgb(37,153,138)"
    let headerColour = UIColor.rgb(red: 246, green: 246, blue: 246, alpha: 1)
    let textBlueColor = UIColor.rgb(red: 0, green: 44, blue: 86, alpha: 1)
    let brightBlueColor = UIColor.rgb(red: 15, green: 80, blue: 251, alpha: 1)
    let textGreyColor = UIColor.rgb(red: 99, green: 109, blue: 131, alpha: 1)
    let viewBlueBackground = UIColor.rgb(red: 0, green: 51, blue: 164, alpha: 1)
    let tableAccentColor = UIColor.rgb(red: 217, green: 234, blue: 253, alpha: 1)
    let tablePrimaryTextColor = UIColor.rgb(red: 1, green: 35, blue: 102, alpha: 1)
    let tableSecondaryTextColor = UIColor.rgb(red: 66, green: 66, blue: 76, alpha: 1)
    let withDrawDescriptionColor = UIColor.rgb(red:81, green: 122, blue:132, alpha: 1)
    let confirmationAmountLightColor = UIColor.rgb(red:179, green: 193, blue:199, alpha: 1)
    let confirmationWithDrawAmountLightColor = UIColor.rgb(red:163, green: 178, blue:253, alpha: 1)
    
    
    // Alternate Colours
    let alternateGrayDark = UIColor.rgb(red: 110, green: 110, blue: 109, alpha: 1)
    let alternateGrayMid = UIColor.rgb(red: 164, green: 163, blue: 163, alpha: 1)
    let warningRed = UIColor.rgb(red: 181, green: 66, blue: 66, alpha: 1)
    
    
    // MARK: - button colors
    let buttonBackGroundColor = UIColor.rgb(red: 0, green: 128, blue: 144, alpha: 1)
    let buttonTextColor = UIColor.rgb(red: 255, green: 255, blue: 255, alpha: 1)
    
    // MARK: - UILabel color
    let labelPrimaryColor = UIColor.rgb(red: 0, green: 0, blue: 0, alpha: 1)  //black
    let labelSecondaryColor  = UIColor.rgb(red: 192, green: 192, blue: 192, alpha: 1)  //light gray
    
    
    //MARK: - TEXT color
    let textPrimaryColor = UIColor.rgb(red: 0, green: 0, blue: 0, alpha: 1)  //black
    let textPlaceholderColor  = UIColor.rgb(red: 192, green: 192, blue: 192, alpha: 1)  //light gray
    
    // MARK:- Navigation bar
    let settingsIcon = UIImage(named: "menu ")?.withRenderingMode(.alwaysTemplate)
    let backIcon = UIImage(named: "chevron-left")?.withRenderingMode(.alwaysTemplate)
    
    let navigationTitlelogo = UIImage(named: "Hipster_NavLogo")?.withRenderingMode(.alwaysOriginal)  //"Hipster_NavLogo"   //"nav_logo2"
    
    
    
}
