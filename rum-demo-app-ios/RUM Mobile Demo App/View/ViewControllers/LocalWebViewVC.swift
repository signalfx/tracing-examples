//
//  LocalWebViewVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 17/02/22.
//

import UIKit
import WebKit

class LocalWebViewVC: UIViewController {}
//Commented Webview related code due to the functions used to detect the click of HTML buttons are not working in Xcode version < 13. Due this, app is throwing compile time errors for Xcode < 13.0
/*
    
    @IBOutlet weak var webView : WKWebView?
    
    //MARK: -
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: false)
        
        self.loadLocalWebView()
    }
    
    //MARK: - Load WebView
    
    func loadLocalWebView() {
        
        guard let localHTMLPath = Bundle.main.path(forResource: "index", ofType: "html") else {return}
        
        DispatchQueue.main.async {
            let contentController = WKUserContentController();
            contentController.add(
                self,
                name: "callbackHandler"
            )

            let config = WKWebViewConfiguration()
            config.userContentController = contentController

            let localwebView = WKWebView(frame: self.webView?.bounds ?? .zero, configuration: config)
            localwebView.navigationDelegate = self
            
            
            APProgressHUD.shared.showProgressHUD()
            self.webView?.addSubview(localwebView)
            let localPathURL = URL.init(fileURLWithPath: localHTMLPath)
            let request = URLRequest.init(url: localPathURL)
            localwebView.navigationDelegate = self
            localwebView.uiDelegate = self
            localwebView.load(request)
        }
        
    }
    
}

@available(iOS 13.0.0, *)
extension LocalWebViewVC : WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler {
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        APProgressHUD.shared.dismissProgressHUD()
    }
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage)
        {
            if(message.body as? String ?? "" == "helloClicked") {
                APToast.showToastWith(message: "Hello iOS User!!")
            } else if(message.body as? String ?? "" == "goodbyeClicked") {
                APToast.showToastWith(message: "Goodbye iOS!")
            }
        }
}

*/
