//
//  CheckOutVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit
import SplunkOtel

class CheckOutVC  : UIViewController{
    
    @IBOutlet weak var btnPlaceOrder: RoundedCornerButton!
        
    @IBOutlet weak var lblCheckOut: UILabel!
    
    @IBOutlet weak var txtEmailAddress: DesignableUITextField!
    
    @IBOutlet weak var txtStreetAddress: DesignableUITextField!
    
    @IBOutlet weak var txtZipCode: DesignableUITextField!
    
    @IBOutlet weak var txtCountry: DesignableUITextField!
    
    @IBOutlet weak var txtCreditCardNumber: DesignableUITextField!
    
    @IBOutlet weak var txtCity: DesignableUITextField!
    @IBOutlet weak var txtState: DesignableUITextField!
    
    @IBOutlet weak var txtCVV: DesignableUITextField!
    
    @IBOutlet weak var txtYear: DesignableUITextField!
    @IBOutlet weak var txtMonth: DesignableUITextField!
    
    @IBOutlet weak var scrollView: UIScrollView!
    
    var checkoutViewModel = CheckOutVM()
    
    var dateMonthPicker : UIPickerView!
    var isMonth : Bool!
    
    var arrTextFiled : [txtValidation]! = [.email,.streetAddress,.zipcode,.country,.state,.city,.ccNumber,.month,.year,.cvv]
    
    let monthArr = ["01","02","03","04","05","06","07","08","09","10","11","12"]
    let yearArr = ["2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        dateMonthPicker = UIPickerView()
        dateMonthPicker.delegate = self
        dateMonthPicker.dataSource = self
        txtMonth.inputView = dateMonthPicker
        txtYear.inputView = dateMonthPicker
        scrollView.addShadow()
        
        txtCreditCardNumber.addTarget(self, action: #selector(didChangeText(textField:)), for: .editingChanged)
        txtCreditCardNumber.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: false)
        btnPlaceOrder.addTextSpacing()
        self.addDefaultInputValues()
        
        RumEventHelper.shared.trackCustomRumEventFor(.checkout)
    }
    
    @objc func didChangeText(textField:UITextField) {
        textField.text = self.modifyCreditCardString(creditCardString: textField.text!)
    }
    
    func modifyCreditCardString(creditCardString : String) -> String {
         let trimmedString = creditCardString.components(separatedBy: "-").joined()

         let arrOfCharacters = Array(trimmedString)
         var modifiedCreditCardString = ""

         if(arrOfCharacters.count > 0) {
             for i in 0...arrOfCharacters.count-1 {
                 modifiedCreditCardString.append(arrOfCharacters[i])
                 if((i+1) % 4 == 0 && i+1 != arrOfCharacters.count){
                     modifiedCreditCardString.append("-")
                 }
             }
         }
         return modifiedCreditCardString
     }

    @IBAction func btnPlaceOrderClicked(_ sender: Any) {
        self.view.endEditing(true)
        guard validateInputs() else { return }
        
        RumEventHelper.shared.trackCustomRumEventFor(.placeOrder)
        
        if self.txtCreditCardNumber.text == "0000-0000-0000-0000" {
            //Generate payment failure exception
            RumEventHelper.shared.addError(RumEventHelper.RumCustomEvent.paymentFailed.rawValue, attributes: ["error" : StringConstants.paymentFailedDueToCC])
            self.showAlertNativeSingleAction(StringConstants.paymentFailed, message: StringConstants.paymentFailedDueToCC)
        } else if RumEventHelper.shared.shouldFailPayment {
            //Generate payment failure exception
            RumEventHelper.shared.addError(RumEventHelper.RumCustomEvent.paymentFailed.rawValue, attributes: ["error" : StringConstants.paymentFailedDueToLocation])
            self.showAlertNativeSingleAction(StringConstants.paymentFailed, message: StringConstants.paymentFailedDueToLocation)
        }else {
            checkoutViewModel.callCheckoutAPI(email: self.txtEmailAddress.text ?? "", streetAddress: self.txtStreetAddress.text ?? "", zipCode: self.txtZipCode.text ?? "", city: self.txtCity.text ?? "", state: self.txtState.text ?? "", country: self.txtCountry.text ?? "", creditCarNumber: self.txtCreditCardNumber.text ?? "", creditCardExpMonth: self.txtMonth.text ?? "", creditCardExpYear: self.txtYear.text ?? "", creditCardCVV: self.txtCVV.text ?? "") { errorMessage in
                
                DispatchQueue.main.async {
                    if let error = errorMessage {
                        RumEventHelper.shared.addError(RumEventHelper.RumCustomEvent.paymentFailed.rawValue, attributes: nil)
                        self.showAlertNativeSingleAction("Failed!", message: error)
                    } else {
                        let vc = mainStoryBoard.instantiateViewController(withIdentifier: "CompleteOrderVC")
                        self.navigationController?.pushViewController(vc, animated: true)
                    }
                }
            }
        }
    }
    
    /**
     Set the default text inputs on the required text fields and allow users to pass the validation without entering any data manually on the screen.
     */
    func addDefaultInputValues() {
        self.txtEmailAddress.text = "someone@example.com"
        self.txtStreetAddress.text = "1600 Amphitheatre Parkway"
        self.txtZipCode.text = "94043"
        self.txtCountry.text = "United States"
        self.txtState.text = "CA"
        self.txtCity.text = "Mountain View"
        self.txtCreditCardNumber.text = "4432-8015-6152-0454"
        self.txtMonth.text = "01"
        self.txtYear.text = "2023"
        self.txtCVV.text = "123"
    }
    
    /**
     *description: Validates user inputs of all the fields.
    */
    func validateInputs() -> Bool {
        // true means there is no error, false means there is an error
        for validation in arrTextFiled {
            switch validation {
            case .email:
                let status = txtEmailAddress.text!.validateEmail()
                guard !status else {continue} //found error then return the status and show error
                self.showValidationError(textfield: txtEmailAddress, errorMessage: "Please enter valid email")
                return status
            case .streetAddress:
                let status = txtStreetAddress.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtStreetAddress, errorMessage: "Please enter valid street address")
                return !status
            case .zipcode:
                var status : Bool! = false
                let count = txtZipCode.text!.count
                if count < 4 || count > 5 {
                    // Zipcode length will be 4 or 5.
                    status = true
                }
                guard status else {continue}
                self.showValidationError(textfield: txtZipCode, errorMessage: "Please enter valid zipcode")
                return !status
            case .country:
                let status = txtCountry.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtCountry, errorMessage: "Please neter country name")
                return !status
            case .state:
                let status = txtState.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtState, errorMessage: "Please enter state name")
                return !status
            case .city:
                let status = txtCity.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtCity, errorMessage: "Please enter city name")
                return !status
            case .ccNumber:
                var status : Bool! = false
                let count = txtCreditCardNumber.text!.count
                if count < 19 {
                    // cc number length will be 19 with seprated character.
                    status = true
                }
                guard status else {continue}
                self.showValidationError(textfield: txtCreditCardNumber, errorMessage: "Please enter valid credit card number")
                return !status
            case .month:
                let status = txtMonth.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtMonth, errorMessage: "Please select month")
                return !status
            case .year:
                let status = txtYear.text! == ""
                guard status else {continue}
                self.showValidationError(textfield: txtYear, errorMessage: "Please select year")
                return !status
            case .cvv:
                var status : Bool! = false
                let count = txtCVV.text!.count
                if count < 3 || count > 3 {
                    // CVV length will be 3.
                    status = true
                }
                guard status else {continue}
                self.showValidationError(textfield: txtCVV, errorMessage: "Please enter valid CVV number")
                return !status
            }
        }
        
        return true
    }
    
    func showValidationError(textfield:DesignableUITextField, errorMessage:String) {
        textfield.layer.borderColor = UIColor.red.cgColor
        let alertAction = UIAlertAction(title: "Ok", style: .default) { alert in
            self.dismiss(animated: true, completion: nil)
        }
        self.showActionSheet(title: "Error", message: errorMessage, handlers: [alertAction])
    }
}

extension CheckOutVC : UITextFieldDelegate {
    
    func textFieldDidBeginEditing(_ textField: UITextField)
    {
        var selectedValuIndex = 0
        let val = textField.text
        
        if textField == txtMonth {
            isMonth = true
            selectedValuIndex = monthArr.firstIndex(of: (val ?? monthArr.first)!) ?? 0
        } else if textField == txtYear {
            isMonth = false
            selectedValuIndex = yearArr.firstIndex(of: (val ?? yearArr.first)!) ?? 0
        }
        
        if textField == txtMonth || textField == txtYear {
            dateMonthPicker.reloadAllComponents()
            dateMonthPicker.selectRow(selectedValuIndex, inComponent: 0, animated: false)
        }
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        textField.layer.borderColor = UIColor.black.cgColor
        return true
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
         let newLength = (textField.text ?? "").count + string.count - range.length
         if(textField == txtCreditCardNumber) {
             return newLength <= 19
         }
         return true
    }

    
}

extension CheckOutVC : UIPickerViewDelegate, UIPickerViewDataSource{
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return isMonth ? monthArr.count : yearArr.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return isMonth ? monthArr[row] : yearArr[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        guard isMonth else { txtYear.text = yearArr[row]; return }
        txtMonth.text = monthArr[row]
    }
    
}
