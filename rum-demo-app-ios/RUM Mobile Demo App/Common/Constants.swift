//
//  Constants.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation

struct Constants {
    
    static let NavigationBarHeight: Float = 64
    static let NavigationBarHeightNotch: Float = 94
    static let NavigationBarButtonHeight: Float = 40
    static let NavigationBarButtonWidth: Float = 40
    
    static let ViewStartPosition : Float = NavigationBarHeight + 20 
    
    static let PasswordeMaxLength: Int = 20
    static let UserNameMaxLength: Int = 20
    static let DefaultMaxLength: Int = 40
   
    static let OnlyAlphabetsWithSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz "
    static let OnlyAlphaNumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz"
    static let OnlyAlphaNumericWithSpace = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz "

   
    static let DefaultCurrencyCode = "USD"
}

enum UserDefaultKeys : String {
    
    case appBaseURL = "UserDefaults_App_Base_URL"
    case storeConfigURL = "PREF_Store_Config_URL"
    case realm = "PREF_Realm"
    case token = "PREF_Rum_Token"
    case beaconURL = "PREF_Beacon_URL"
    case rumURL = "PREF_Rum_URL"
    case appName = "PREF_Application_Name"
    case environmentName = "PREF_Environment_Name"
}


class AppVariables {
    
    static var current = AppVariables()
    
    var configURL : String {
        get {
            return (AppDefaults.getValue(for: .storeConfigURL) as? String) ?? Configuration().rootAPIUrl
        }
        
        set {
            AppDefaults.setValue(newValue, for: .storeConfigURL)
        }
    }
    var rumURL : String {
        get {
            return (AppDefaults.getValue(for: .rumURL) as? String) ?? Configuration().rumURL
        }
        
        set {
            AppDefaults.setValue(newValue, for: .rumURL)
        }
    }
    
    var applicationName : String {
        get {
            return (AppDefaults.getValue(for: .appName) as? String) ?? Configuration().appName
        }
        
        set {
            AppDefaults.setValue(newValue, for: .appName)
        }
    }
    
    var realm : String {
        get {
            return (AppDefaults.getValue(for: .realm) as? String) ?? Configuration().realmValue
        }
        
        set {
            AppDefaults.setValue(newValue, for: .realm)
        }
    }
    
    var token : String {
        get {
            return (AppDefaults.getValue(for: .token) as? String) ?? Configuration().rumAuth
        }
        
        set {
            AppDefaults.setValue(newValue, for: .token)
        }
    }
    var environment : String {
        get {
            return (AppDefaults.getValue(for: .environmentName) as? String) ?? Configuration().rumEnvironmentName
        }
        
        set {
            AppDefaults.setValue(newValue, for: .environmentName)
        }
    }
    var beaconURL : String {
        get {
            return "https://rum-ingest.\(self.realm).signalfx.com/v1/rum"
        }
    }
    
}

class AppDefaults {
    
    class func getValue(for key: UserDefaultKeys) -> Any? {
        return UserDefaults.standard.value(forKey: key.rawValue)
    }
    
    class func setValue(_ value: Any, for key: UserDefaultKeys) {
        UserDefaults.standard.set(value, forKey: key.rawValue)
        UserDefaults.standard.synchronize()
    }
    
    class func removeValue(for key: UserDefaultKeys) {
        UserDefaults.standard.removeObject(forKey: key.rawValue)
        UserDefaults.standard.synchronize()
    }
    
}
