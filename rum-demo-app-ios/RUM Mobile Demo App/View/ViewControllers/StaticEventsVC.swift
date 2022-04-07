//
//  StaticEventsVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import WebKit
import SplunkOtel


class StaticEventsVC : UIViewController{
    
    @IBOutlet weak var btnCrashApp: RoundedCornerButton?
    @IBOutlet weak var btnAnr: RoundedCornerButton!
    @IBOutlet weak var btnFourHundredError: RoundedCornerButton!
    @IBOutlet weak var btnFiveHundredError: RoundedCornerButton!
    
    @IBOutlet weak var btnLoadWebView: RoundedCornerButton!
    
    @IBOutlet weak var btnException: RoundedCornerButton!
    
    @IBOutlet weak var btnSlowApiResponse: RoundedCornerButton!
    
    @IBOutlet weak var btnShopWebView: RoundedCornerButton!
    
    @IBOutlet weak var btnLocalWebView: RoundedCornerButton!
    
    // MARK: - Injection
    let viewModel = StaticEventsVM()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: true)
        btnCrashApp?.addTextSpacing()
        btnFourHundredError.addTextSpacing()
        btnFiveHundredError.addTextSpacing()
        btnException.addTextSpacing()
        btnSlowApiResponse.addTextSpacing()
        btnLocalWebView.addTextSpacing()
        btnShopWebView.addTextSpacing()
    }
    
    // MARK: - Networking
    private func attemptNetwrokCall(withcode : Int) {
        viewModel.staticEvent(withcode: withcode)
    }
    
    //MARK: - Actions
     @IBAction func btnCrashAppClicked(_ sender: Any) {
         viewModel.crashApp()
    }

    @IBAction func btnFourHundredErrorClicked(_ sender: Any) {
        attemptNetwrokCall(withcode: 400)
    }
    
    @IBAction func btnFiveHundredErrorClicked(_ sender: Any) {
        attemptNetwrokCall(withcode: 500)
    }
    
    
    @IBAction func btnExceptionClicked(_ sender: Any) {
        do {
            let htmlPath = Bundle.main.path(forResource: "sample4", ofType: "html")

            try isFileAvailableAt(resourcePath: htmlPath ?? "sample4.html")
        }
        catch CustomError.notFound {
            handleException(errorstring: "File not exist.")  // common function in apputils file
        }
        catch {
            //other error
        }
    }
    @IBAction func btnSlowApiResponseClicked(_ sender: Any){
        attemptSlowApiResponse()
    }
    
    @IBAction func btnLocalWebViewClicked(_ sender: Any){
        DispatchQueue.main.async {
            let vc = mainStoryBoard.instantiateViewController(withIdentifier: "LocalWebViewVC") as! LocalWebViewVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    @IBAction func btnShopWebViewClicked(_ sender: Any){
        DispatchQueue.main.async {
            let vc = mainStoryBoard.instantiateViewController(withIdentifier: "ShopWebViewVC") as! ShopWebViewVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    
    //MARK: -
    
    private func attemptSlowApiResponse() {
        viewModel.slowApiResponse()
    }
    
    func isFileAvailableAt(resourcePath : String) throws {
        if FileManager.default.fileExists(atPath: resourcePath){
            print("file is exist at path")
        }
        else{
            throw CustomError.notFound
        }
    }
    
}
