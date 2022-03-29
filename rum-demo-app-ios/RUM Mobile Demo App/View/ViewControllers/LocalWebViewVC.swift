//
//  LocalWebViewVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 17/02/22.
//

import UIKit
import WebKit
import SplunkOtel

class LocalWebViewVC: UIViewController {
    
    @IBOutlet weak var webView : WKWebView?
    
    //MARK: -Variable
    var jsonUpdate:String = ""
    var isFirstTimeLoad :Bool = false
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
           // localwebView.navigationDelegate = self
            
            APProgressHUD.shared.showProgressHUD()
            self.webView?.addSubview(localwebView)
            let localPathURL = URL.init(fileURLWithPath: localHTMLPath)
            let request = URLRequest.init(url: localPathURL)
            localwebView.navigationDelegate = self
            localwebView.uiDelegate = self
            localwebView.load(request)
            
            
            //Initialise RUM SDK
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                let dict = [
                    "realm": AppVariables.current.realm,
                    "token": AppVariables.current.token,
                    "sessionId": SplunkRum.getSessionId()
                ]
                let jsonData = try! JSONSerialization.data(withJSONObject: dict)
                
                let jsonString = String(data: jsonData, encoding: .utf8)!
                print(jsonString)
                self.jsonUpdate = jsonString
//                let javaScript = "initialiseRUM(\'\(jsonString)')"
//
//                localwebView.evaluateJavaScript(javaScript) { result, error in
//                    guard error == nil else {
//                        print(error ?? "Failed to evaluate JS")
//                        return
//                    }
//                }
            }
        }
        
    }
    
}

extension LocalWebViewVC : WKNavigationDelegate, WKUIDelegate, WKScriptMessageHandler {
   
    
    
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        APProgressHUD.shared.dismissProgressHUD()
        print("didFinish navigation")
        if !isFirstTimeLoad{
            self.isFirstTimeLoad = true
            webView.evaluateJavaScript("initialiseRUM(\'\(self.jsonUpdate)')") { result, error in
                                guard error == nil else {
                                    print(error ?? "Failed to evaluate JS")
                                    return
                                }
            }
        }
       
        webView.evaluateJavaScript("setSessionID(\'\(SplunkRum.getSessionId())')") { result, error in
            guard error == nil else {
                print(error ?? "Failed to evaluate JS")
                return
            }
        }
    }
    
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage)
    {
        print("WEBVIEW ==>> \(message.body as? String ?? "")")
        if(message.body as? String ?? "" == "helloClicked") {
            APToast.showToastWith(message: "Hello iOS User!!")
        } else if(message.body as? String ?? "" == "goodbyeClicked") {
            APToast.showToastWith(message: "Goodbye iOS!")
        }
    }
    
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        print("didStartProvisionalNavigation\n\(webView.url?.absoluteString ?? "NA")")
    }
}

