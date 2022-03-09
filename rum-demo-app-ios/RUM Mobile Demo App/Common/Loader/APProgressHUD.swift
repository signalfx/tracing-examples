//
//  APProgressHUD.swift
//  Ready Vendor
//
//  Created by Akash Patel on 13/01/22.
//

import Foundation
import UIKit

class APProgressHUD {
    
    var loadingView = APProgressView()
    
    static let shared = APProgressHUD()
    public func showProgressHUD(_ title : String? = nil) {
        DispatchQueue.main.async {
            self.loadingView = Bundle.main.loadNibNamed("APProgressView", owner: nil, options: nil)?[0] as? APProgressView ?? APProgressView()
            self.loadingView.frame = CGRect.init(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
            (UIApplication.shared.delegate as? AppDelegate)?.window?.addSubview(self.loadingView)
            self.loadingView.startLoadingWith(title)
        }
    }
    
    public func dismissProgressHUD() {
        DispatchQueue.main.async {
            self.loadingView.removeFromSuperview()
        }
    }
}


class APProgressView : UIView {
    
    @IBOutlet weak var viewLoaderContainer : UIView?
    @IBOutlet weak var lblLoading : UILabel?
    @IBOutlet weak var activityLoader : UIActivityIndicatorView?
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func startLoadingWith(_ loadingMessage : String? = nil) {
        
        self.viewLoaderContainer?.backgroundColor = UIColor.init(hexString: "17A2B8")
        self.activityLoader?.startAnimating()
        if let message = loadingMessage {
            self.lblLoading?.isHidden = false
            self.lblLoading?.text = message
        } else {
            self.lblLoading?.isHidden = true
        }
    }
}
