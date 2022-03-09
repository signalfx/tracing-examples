//
//  URLConfigurationVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import OpenTelemetryApi
import OpenTelemetrySdk
import SplunkOtel
import SplunkOtelCrashReporting

class URLConfigurationVC : UIViewController {
    
    
    @IBOutlet weak var lblTitle: UILabel!
    @IBOutlet weak var btnSubmit: UIButton!
    @IBOutlet weak var btnDelayedLogin: UIButton!
    @IBOutlet weak var txtURL: DesignableUITextField!
    @IBOutlet weak var lblErrorMessage: UILabel!
    @IBOutlet weak var txtToken: DesignableUITextField!
    @IBOutlet weak var lblErrorMessageForToken: UILabel!
    @IBOutlet weak var txtEnvName: DesignableUITextField!
    @IBOutlet weak var lblErrorMessageEnvName: UILabel!
    @IBOutlet weak var txtRealm: UITextField!
    @IBOutlet weak var viewRealm: UIView!
    @IBOutlet weak var txtAppName: DesignableUITextField!
    @IBOutlet weak var lblErrorMessageForAppName: UILabel!
    @IBOutlet weak var lblRealmTitle: UILabel!
    
    var arrayRealm = ["us0", "us1", "us2", "eu0", "lab0", "rc0"]
    var realmPickerView = UIPickerView()
    
    @IBOutlet weak var imgGlobe: UIImageView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setupUI()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let scaledFontSize = (UIScreen.main.bounds.width * 0.028) //This sets the font size depending on the device's screen
        btnSubmit.addTextSpacing(fontsize: scaledFontSize)
        btnDelayedLogin.addTextSpacing(fontsize: scaledFontSize)
        
        AppDefaults.removeValue(for: .appBaseURL)
    }
    
    
    /// This function set the UI and default values of the input fields of the RUM configurations.
    func setupUI() {
        
        self.viewRealm.layer.cornerRadius = 10
        self.viewRealm.layer.masksToBounds = true
        self.viewRealm.layer.borderColor = UIColor.darkGray.cgColor
        self.viewRealm.layer.borderWidth = 1
        
        self.txtRealm.inputView = self.realmPickerView
        self.realmPickerView.delegate = self
        self.realmPickerView.dataSource = self
        self.lblRealmTitle.addTextSpacing(spacing: 3)
        
        self.lblErrorMessageEnvName.isHidden = true
        self.lblErrorMessageForToken.isHidden = true
        self.lblErrorMessageForAppName.isHidden = true
        self.lblErrorMessage.isHidden = true
        
        //Set default values
        self.txtURL.text = AppVariables.current.configURL
        self.txtEnvName.text = AppVariables.current.environment
        self.txtAppName.text = AppVariables.current.applicationName
        self.txtRealm.text = AppVariables.current.realm
        self.realmPickerView.selectRow(arrayRealm.firstIndex(of: AppVariables.current.realm) ?? 0, inComponent: 0, animated: true)
    }
    
    
    /// This function enable/disable the Submit button based on valid inputs of store url.
    func setSubmitButtonStatus(){
        if txtURL.text?.isEmpty ?? true {
            btnSubmit.isUserInteractionEnabled = false
            btnSubmit.backgroundColor = config.disableButtonBgColor
            btnDelayedLogin.isUserInteractionEnabled = false
            btnDelayedLogin.backgroundColor = config.disableButtonBgColor
        }
        else{
            btnSubmit.isUserInteractionEnabled = true
            btnSubmit.backgroundColor =  config.commonScreenBgColor
            btnDelayedLogin.isUserInteractionEnabled = true
            btnDelayedLogin.backgroundColor = config.commonScreenBgColor
        }
        
    }
    
    
    /// This function validate the inputs entered by user for RUM configuration and returns boolean value based on validation passed or failed.
    /// - Returns: TRUE if input is valid and FALSE if not.
    func validateInputs() -> Bool {
        
        var validInputs = true
        var urlToCheck = self.txtURL.text
        
        if urlToCheck?.last == "/" {
            urlToCheck?.removeLast()
        }
        
        if urlToCheck?.count == 0 {
            self.txtURL.layer.borderColor = UIColor.red.cgColor
            self.lblErrorMessage.isHidden = false
            self.lblErrorMessage.text = StringConstants.noURLMsg
            validInputs = false
        }
        else if !(urlToCheck?.isValidUrl() ?? false) {
            
            if let url = URL.init(string: urlToCheck ?? ""), UIApplication.shared.canOpenURL(url) {
                self.lblErrorMessage.isHidden = true
                validInputs = true
            }
            
            self.txtURL.layer.borderColor = UIColor.red.cgColor
            self.lblErrorMessage.isHidden = false
            self.lblErrorMessage.text = StringConstants.urlIsNotProperMsg
            validInputs = false
        }
        else{
            self.lblErrorMessage.isHidden = true
        }
        
        return validInputs
    }
    
    
    /// This function stores the variables in User Defaults to use it for RUM SDK Initialisation on the next launch of app.
    func setAppVariables() {
        
        var urlToCheck = self.txtURL.text
        
        if urlToCheck?.last != "/" {
            urlToCheck = urlToCheck?.appending("/")
        }
        
        //Set values to app variables
        AppVariables.current.configURL = urlToCheck ?? Configuration().rootAPIUrl
        AppVariables.current.realm = self.txtRealm.text ?? Configuration().realmValue
        
        if self.txtAppName.text?.isEmpty ?? true {
            AppVariables.current.applicationName = Configuration().appName
        } else {
            AppVariables.current.applicationName = self.txtAppName.text ?? Configuration().appName
        }
        
        if self.txtToken.text?.isEmpty ?? true {
            AppVariables.current.token = Configuration().rumAuth
        } else {
            AppVariables.current.token = self.txtToken.text ?? Configuration().rumAuth
        }
        
        if self.txtEnvName.text?.isEmpty ?? true {
            AppVariables.current.environment = Configuration().rumEnvironmentName
        } else {
            AppVariables.current.environment = self.txtEnvName.text ?? Configuration().rumEnvironmentName
        }
        
    }
    
    
    /// This function returns the boolean value based on values are changed in the input fields after launching the application. This is generally used to decide if needs to update the values in User Defaults or not.
    /// - Returns: TRUE if user has updated any input field values after the app launch, otherwise FALSE.
    func didUserChangeConfiguration() -> Bool {
        
        if self.txtURL.text != AppVariables.current.configURL {
            return true
        }
        
        if self.txtEnvName.text != AppVariables.current.environment {
            return true
        }
        
        if self.txtAppName.text != AppVariables.current.applicationName {
            return true
        }
        
        if self.txtRealm.text != AppVariables.current.realm {
            return true
        }
        
        return false
    }
    
    
    /// Redirect user to Products List screen.
    func navigateToProductList() {
        let mainTabBarController = mainStoryBoard.instantiateViewController(withIdentifier: "MainTabBarController")
        (UIApplication.shared.delegate as? AppDelegate)?.changeRootViewController(mainTabBarController)
    }
    
    //MARK: - Actions
    
    @IBAction func btnRealmClicked(_ sender : UIButton) {
        self.view.endEditing(true)
        self.txtRealm.becomeFirstResponder()
    }
    
    @IBAction func btnSubmitClicked(_ sender: Any) {
        self.view.endEditing(true)
        if self.validateInputs() {
            if self.didUserChangeConfiguration() {
                self.setAppVariables()
                self.view.isUserInteractionEnabled = false
                APToast.showToastWith(message: StringConstants.restartAppToApplyConfigChanges, screenTime: 3.0) {
                    self.view.isUserInteractionEnabled = true
                    RumEventHelper.shared.trackCustomRumEventFor(.timeToReady)
                    self.navigateToProductList()
                }
            } else {
                RumEventHelper.shared.trackCustomRumEventFor(.timeToReady)
                self.navigateToProductList()
            }
        }
    }
    
    @IBAction func btnDelayedLoginClicked(_ sender : UIButton) {
        
        if validateInputs() {
            RumEventHelper.shared.startSpanWith(spanName: RumEventHelper.RumCustomEvent.timeToReady.rawValue, shouldCreateWorkflow: true, parentSpan: nil, attributes: nil) { timeToReadySpan in
                StaticEventsVM().slowApiResponse(4) {
                    StaticEventsVM().slowApiResponse(4) {
                        
                        APProgressHUD.shared.showProgressHUD(nil)
                        
                        self.addDelayedSpan(2) {
                            timeToReadySpan?.end()
                            APProgressHUD.shared.dismissProgressHUD()
                            
                            if self.didUserChangeConfiguration() {
                                self.setAppVariables()
                                self.view.isUserInteractionEnabled = false
                                APToast.showToastWith(message: StringConstants.restartAppToApplyConfigChanges, screenTime: 3.0) {
                                    self.view.isUserInteractionEnabled = true
                                    RumEventHelper.shared.trackCustomRumEventFor(.timeToReady)
                                    self.navigateToProductList()
                                }
                            } else {
                                RumEventHelper.shared.trackCustomRumEventFor(.timeToReady)
                                self.navigateToProductList()
                            }
                        }
                    }
                }
            }
        }
    }
    
    //MARK: -
    
    /// This function creates the Span with delay of predefined time. This is used to demonstrate the Nested Span on Observability Cloud.
    /// - Parameters:
    ///   - count: The count stands for number of Span pair should created nested. For example, 1 means one pair (2) of nested Span will be created.
    ///   - completion: This callback used to perform further actions when Span duration is over.
    func addDelayedSpan(_ count : Int, completion : @escaping ()->Void) {
        if count > 0, let activeSpan = OpenTelemetry.instance.contextProvider.activeSpan {
            RumEventHelper.shared.startSpanWith(spanName: "dashboard_list_favorite_load_time", parentSpan: activeSpan, attributes: nil) { parentSpan in
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
                    parentSpan?.end()
                    
                    RumEventHelper.shared.startSpanWith(spanName: "dashboard_list_custom_load_time", parentSpan: parentSpan, attributes: nil) { childSpan in
                        
                        DispatchQueue.main.asyncAfter(deadline: .now() + 4.0) {
                            childSpan?.end()
                            
                            self.addDelayedSpan(count - 1, completion: completion)
                        }
                    }
                }
            }
        } else {
            completion()
        }
    }
}

extension URLConfigurationVC : UITextFieldDelegate{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
            self.setSubmitButtonStatus()
        }
        
        if let text = textField.text,
           let textRange = Range(range, in: text) {
            let updatedText = text.replacingCharacters(in: textRange,
                                                       with: string)
            if !self.lblErrorMessage.isHidden && updatedText.isValidUrl() {
                self.txtURL.layer.borderColor = UIColor.black.cgColor
                self.lblErrorMessage.isHidden = true
            }
            
        }
        
        return true
    }
}

extension URLConfigurationVC: UIPickerViewDelegate, UIPickerViewDataSource {
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }

    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return arrayRealm.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return arrayRealm[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        self.txtRealm.text = arrayRealm[row]
    }

}
