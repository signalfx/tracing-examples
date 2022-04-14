//
//  AppDelegate.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import CoreLocation
import SplunkOtel
import SplunkOtelCrashReporting
import IQKeyboardManager


let config = Configuration()
let mainStoryBoard = UIStoryboard(name: "Main", bundle: nil)
let screenSize: CGRect = UIScreen.main.bounds
let screenWidth = screenSize.width
let screenHeight = screenSize.height

var coordinate = CLLocationCoordinate2D()
var placemarkDescription = CLPlacemark()

let geoCoder = CLGeocoder()
let sharedCart = Cart.sharedInstance  //shared instance of cart class

var timerForLocation : Timer?

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    let locationObj = CLLocationManager()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        window?.backgroundColor = .white
        IQKeyboardManager.shared().isEnabled = true
       
        locationObj.delegate = self
        locationObj.requestWhenInUseAuthorization()
        
        if #available(iOS 14.0, *) {
            if locationObj.authorizationStatus == CLAuthorizationStatus.authorizedWhenInUse {
                locationObj.startUpdatingLocation()
            }
        } else {
            // Fallback on earlier versions
            if CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedWhenInUse {
                locationObj.startUpdatingLocation()
            }
        }
                
    
        SplunkRum.initialize(beaconUrl: AppVariables.current.beaconURL , rumAuth: AppVariables.current.token ,options: SplunkRumOptions(debug: true, environment: AppVariables.current.environment))
            
            SplunkRumCrashReporting.start()
            SplunkRum.setGlobalAttributes(["DeviceID": UIDevice.current.identifierForVendor?.uuidString as Any])
            //https://ingest.us1.signalfx.com  -- realm URL
            //https://rum-ingest.us0.signalfx.com/v1/rum  -- default
        
       
        //Initialise the timer to get the latest location coordinates on every 5 seconds
        timerForLocation = Timer.scheduledTimer(withTimeInterval: 5.0, repeats: true, block: { timerObj in
            self.startGettingLatestLocation()
        })
        
        //set root view controlle here
        self.window = UIWindow(frame: UIScreen.main.bounds)
        // if user is logged in before
        if UserDefaults.standard.string(forKey: "username") != nil {
            // instantiate the main tab bar controller and set it as root view controller
            // using the storyboard identifier we set earlier
                let mainTabBarController = mainStoryBoard.instantiateViewController(withIdentifier: "MainTabBarController")
                window?.rootViewController = mainTabBarController
            
        } else {
            // if user isn't logged in
            // instantiate the navigation controller and set it as root view controller
            // using the storyboard identifier we set earlier
               let loginNavController = mainStoryBoard.instantiateViewController(withIdentifier: "LoginNavigationController")
                window?.rootViewController = loginNavController
        }
           
        window?.makeKeyAndVisible()
        return true
    }

   
    //MARK:  - Get the location in every 5 seconds
    /**
     To update the latest location coordinates on the Golobal Attributes, this method invokes the location method on every 5 seconds.
     */
    func startGettingLatestLocation() {
        DispatchQueue.main.async {
            if #available(iOS 14.0, *) {
                if self.locationObj.authorizationStatus == CLAuthorizationStatus.authorizedWhenInUse {
                    self.locationObj.startUpdatingLocation()
                }
            } else {
                // Fallback on earlier versions
                if CLLocationManager.authorizationStatus() == CLAuthorizationStatus.authorizedWhenInUse {
                    self.locationObj.startUpdatingLocation()
                }
            }
        }
    }
    

    // MARK: - change root vc
    /**
     *description: Change the root view controller of the window scene.
     *Parameter vc: The view controller which needs to be set as root controller of the window
     *Parameter animated: Whether the window root controller needs to be set with animation or not.
    */
    func changeRootViewController(_ vc: UIViewController, animated: Bool = true) {
        
        DispatchQueue.main.async {
            guard let window = self.window else {
                return
            }
            if  vc is UITabBarController {
                let tabContoller = vc as? UITabBarController
                let itemCount = sharedCart.getItemsCountToShowAsBadge()
                tabContoller?.tabBar.addBadge(atIndex: 1, badge: itemCount)
            }
            
            
            // A mask of options indicating how you want to perform the animations.
            let options: UIView.AnimationOptions = .curveLinear

            // The duration of the transition animation, measured in seconds.
            let duration: TimeInterval = 0.3

            // Creates a transition animation.
            // Though `animations` is optional, the documentation tells us that it must not be nil. ¯\_(ツ)_/¯
            UIView.transition(with: window, duration: duration, options: options, animations: {
                
               UIView.animate(withDuration: 0.3, animations: { () -> Void in
                    window.transform = CGAffineTransform.identity.translatedBy(x: -screenWidth, y: 0)
                }, completion: { (Finished) -> Void in
                    
                })
            }, completion:
            { completed in
                // maybe do something on completion here
                // change the root view controller to your specific view controller
                window.transform = CGAffineTransform.identity
                window.rootViewController = vc
            })
        }
    }

}
extension AppDelegate : CLLocationManagerDelegate{
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        _ = CLLocation(latitude: (manager.location?.coordinate.latitude) ?? 0 , longitude: (manager.location?.coordinate.longitude) ?? 0)
        
        guard let  coord = manager.location?.coordinate else {
            print("Location not proper")
            return
        }
        coordinate = coord
        
        //FRANCE: 46.2276, 2.2137
        //SplunkRum.setGlobalAttributes(["_sf_geo_lat":46.2276,"_sf_geo_long":2.2137])
        SplunkRum.setGlobalAttributes(["_sf_geo_lat":coordinate.latitude as Any,"_sf_geo_long":coordinate.longitude as Any])
        
        locationObj.stopUpdatingLocation()
        RumEventHelper.shared.handleLocationBasedAPICall()
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("LocationManager didFailWithError \(error.localizedDescription)")
           if let error = error as? CLError, error.code == .denied {
              return
           }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse {
            locationObj.requestLocation()
        }
    }
}

