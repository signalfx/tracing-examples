//
//  ShopWebViewVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 17/02/22.
//

import UIKit
import WebKit

class ShopWebViewVC: UIViewController {

    @IBOutlet weak var webView : WKWebView?
    
    //MARK: -
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: false)
        
        self.loadShopWebView()
    }
    
    //MARK: - Load WebView
    
    func loadShopWebView() {
        
        guard let webURL = URL.init(string: AppVariables.current.configURL) else {return}
        
        APProgressHUD.shared.showProgressHUD()
        let request = URLRequest.init(url: webURL)
        self.webView?.navigationDelegate = self
        self.webView?.load(request)
    }

}

extension ShopWebViewVC : WKNavigationDelegate {
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        APProgressHUD.shared.dismissProgressHUD()
    }
}
