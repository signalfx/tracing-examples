//
//  StaticEventsVM.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 17/01/22.
//

import UIKit
import Foundation

class StaticEventsVM {
    
    // MARK: - Properties
    private var staticevent: StaticEvents?{
        didSet {
            self.didFinishFetch?()
        }
    }
    var responsecode: Int = 0
    var error: Error? {
        didSet { self.showAlertClosure?() }
    }
    
    // MARK: - Closures for callback, since we are not using the ViewModel to the View.
    var showAlertClosure: (() -> ())?
    var updateLoadingStatus: (() -> ())?
    var didFinishFetch: (() -> ())?
    
    
    // MARK: - Network call
    /**
     *description: API call for specific error codes of response.
     *Parameter withcode: The error code of expected from the API response code.
    */
    func staticEvent(withcode : Int, productID : String = "66VCHSJNUP", completion: (()->Void)? = nil) {
        if withcode == 400{
            self.generate4xxEndpoitError(completion: completion)
        }
        else{
            self.generate5xxEndpointError(productID : productID, completion: completion)
        }
    }
    
    fileprivate func generate5xxEndpointError(productID : String, completion: (()->Void)? = nil) {
        
        let URL = getURL(for: ApiName.GeneratePayment.rawValue)
        let parameters : [String : Any] = ["product_id" : productID,
                                       "quantity" : 1]
        DataService.request( URL , method: "POST", params:parameters, type: StaticEvents.self) { (staticevent, error , responsecode) in
                self.responsecode = responsecode
                self.staticevent = staticevent
                self.error = error as? Error
                completion?()
        }
    }
    
    fileprivate func generate4xxEndpoitError(completion: (()->Void)? = nil) {
        
        let URL = "\(AppVariables.current.configURL)cart/checkouts"
        DataService.request( URL , method: "GET", params:[:], type: StaticEvents.self) { (staticevent, error , responsecode) in
                self.responsecode = responsecode
                self.staticevent = staticevent
                self.error = error as? Error
                completion?()
        }
    }
    
    // MARK: - Slow Api response
    /**
     *description: API call for cart.
    */
    func slowApiResponse(_ delayTime: Int = 5, completion: (()->Void)? = nil){
        //Change value(seconds) for slow response
        DataService.request( "\(getURL(for: ApiName.GenerateCartEmpty.rawValue))?delay=\(delayTime)" , method: "GET", params:[:], type: StaticEvents.self) { (staticevent, error , responsecode) in
                completion?()
                self.responsecode = responsecode
                self.staticevent = staticevent
                self.error = error as? Error
        }
    }
    
    //MARK: - Crash the app
    /**
     Crash the app forcefully
     */
    
    func crashApp() {
        let null = UnsafePointer<UInt8>(bitPattern: 0)
        _ = null!.pointee
    }
    
    //MARK: - Freeze the app
    /**
     Freeze the user intercation of  the app for specified seconds. This will add one transparent view on the root window and will block the user actions on the screen below it.
     */
    func freezeApp() {
        DispatchQueue.main.async {
            if let appDel = UIApplication.shared.delegate as? AppDelegate {
                let blockerView = UIView.init(frame: appDel.window?.frame ?? .zero)
                appDel.window?.addSubview(blockerView)
                
                //Freeze time is 5.0 seconds
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    blockerView.removeFromSuperview()
                }
            }
        }
    }
}
