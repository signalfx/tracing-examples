//
//  UIViewControllerExtension.swift
//  RUM Mobile Demo App
//
//  Created by Shraddha Hattimare on 30/09/21.
//

import Foundation
import UIKit

let window = UIApplication.shared.keyWindow


extension UIViewController {
    
    func navBarHeight() -> Float{
        if UIDevice.current.hasNotch{
            //notch device
            return Constants.NavigationBarHeightNotch
            
        }
        else{
            //not notch device
            return Constants.NavigationBarHeight
            
        }
    }
    func addBlueHeader(title: String, isRightButtonHidden : Bool , isBackButtonHidden : Bool) {
        
        self.navigationController?.setNavigationBarHidden(true, animated: false)
        self.navigationItem.hidesBackButton = true
        
        let header = UIView()
        header.backgroundColor = .black
        // header.frame = CGRect(x: 0, y: 20, width: screenWidth, height: CGFloat(Constants.NavigationBarHeight))
        let navbarheight = self.navBarHeight()
        
        header.frame = CGRect(x: 0, y: 0, width: screenWidth, height: CGFloat(navbarheight))
        
        
        var yPosition = CGFloat((navbarheight - Constants.NavigationBarButtonHeight)/2)
        
        if UIDevice.current.hasNotch{
            yPosition = 50
        }
        else{
            yPosition = 18  //26
        }
        
        let lbutton = UIButton()
        lbutton.frame = CGRect(x: 5, y: yPosition, width: CGFloat(Constants.NavigationBarButtonWidth), height: CGFloat(Constants.NavigationBarButtonHeight))
        lbutton.backgroundColor = .clear
        lbutton.addTarget(self, action:#selector(gotoPreviousScreen), for: .touchUpInside)
        let image = UIImage(named: "chevron-left")?.withRenderingMode(.alwaysTemplate)
        lbutton.imageView?.contentMode = .scaleAspectFit
        lbutton.tintColor = config.secondaryAccentColor
        lbutton.setImage(image, for: .normal)
        lbutton.isAccessibilityElement = true
        lbutton.accessibilityLabel = "header_back_btn"
        
        
        
        let rbutton = UIButton()
        rbutton.frame = CGRect(x: screenWidth - 45, y: yPosition, width: CGFloat(Constants.NavigationBarButtonWidth), height: CGFloat(Constants.NavigationBarButtonHeight))
        rbutton.backgroundColor = .clear
        rbutton.addTarget(self, action: #selector(gotoURLConfigScreen), for: .touchUpInside)
        let image1 = UIImage(named: "menu")?.withRenderingMode(.alwaysTemplate)
        rbutton.imageView?.contentMode = .scaleAspectFit
        rbutton.tintColor = .white //config.secondaryAccentColor
        rbutton.setImage(image1, for: .normal)
        rbutton.isAccessibilityElement = true
        rbutton.accessibilityLabel = "header_context_menu_icon"
        
        
        let logoImageView = UIImageView()
        logoImageView.contentMode = .scaleAspectFit
        logoImageView.clipsToBounds = true
        logoImageView.backgroundColor = .clear
        logoImageView.image = config.navigationTitlelogo
        logoImageView.frame = CGRect(x: 70 , y: yPosition, width: 211, height: CGFloat(Constants.NavigationBarButtonHeight - 2))
        logoImageView.center.x = self.view.center.x
        
        
        
        header.addSubview(lbutton)
        header.addSubview(rbutton)
        header.addSubview(logoImageView)
        self.view .addSubview(header)
        
        rbutton.isHidden = isRightButtonHidden
        lbutton.isHidden = isBackButtonHidden
        
    }
    
    // MARK: - nav bar button action
    @objc func gotoURLConfigScreen() {
        
        NavMenu.showNavMenu {
            let loginNavController = mainStoryBoard.instantiateViewController(withIdentifier: "LoginNavigationController")
            (UIApplication.shared.delegate as? AppDelegate)?.changeRootViewController(loginNavController)
        }
    }
    @objc func gotoPreviousScreen() {
        navigationController?.popViewController(animated: true)
    }
    
    func showAlertNativeSingleAction(_ title: String, message : String, buttonTitle : String = "Okay", clickHandler : (()->Void)? = nil, dismissCompletion : (()->Void)? = nil) {
        
        DispatchQueue.main.async {
            let alertController = UIAlertController.init(title: title, message: message, preferredStyle: .alert)
            
            let alertAction = UIAlertAction.init(title: buttonTitle, style: .default) { action in
                clickHandler?()
            }
            
            alertAction.isAccessibilityElement = true
            alertAction.accessibilityLabel = "alert_dialogue_single_button_action"
            
            alertController.addAction(alertAction)
            self.presentController(viewController: alertController, animated: true)
        }
    }
    
    func showAlertNativeDoubleAction(_ title: String, message : String, buttonTitle1 : String, clickHandler1 : (()->Void)? = nil, buttonTitle2: String, clickHandler2 : (()->Void)? = nil, dismissCompletion : (()->Void)? = nil) {
        
        DispatchQueue.main.async {
            let alertController = UIAlertController.init(title: title, message: message, preferredStyle: .alert)
            
            let alertAction1 = UIAlertAction.init(title: buttonTitle1, style: .default) { action in
                clickHandler1?()
            }
            
            let alertAction2 = UIAlertAction.init(title: buttonTitle2, style: .default) { action in
                clickHandler2?()
            }
            
            alertAction1.isAccessibilityElement = true
            alertAction1.accessibilityLabel = "alert_dialogue_double_button_action1"
            alertAction2.isAccessibilityElement = true
            alertAction2.accessibilityLabel = "alert_dialogue_double_button_action2"
            
            alertController.addAction(alertAction1)
            alertController.addAction(alertAction2)
            self.presentController(viewController: alertController, animated: true)
        }
    }
    
    
    func showActionSheet(title: String, message : String, handlers: [UIAlertAction]) {
        let alert = UIAlertController(title: title, message: message, preferredStyle: .actionSheet)
        for action in handlers{
            alert.addAction(action)
        }
        self.present(alert, animated: true, completion: nil)
    }
    
    
    func presentController(viewController : UIViewController, animated : Bool)
    {
        if #available(iOS 13.0, *)
        {
            viewController.modalPresentationStyle = .fullScreen
        }
        self.present(viewController, animated: animated, completion: nil)
    }
    
    
    
}

//MARK: - UIBUTTON subclass
class RoundedCornerButton : UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: .zero)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        // set other operations after super.init, if required
        self.layer.cornerRadius = 8
        self.backgroundColor =  config.commonScreenBgColor
        self.clipsToBounds = true
        self.titleLabel?.numberOfLines = 0
        self.frame.size.height = 50
        self.frame.size.width = screenWidth - 40
        self.frame.origin.x = 20
        self.titleLabel?.textAlignment = .center
    }
}

class RoundedCornerButtonNoFrame : UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: .zero)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        
        // set other operations after super.init, if required
        self.layer.cornerRadius = 8
        self.backgroundColor =  config.commonScreenBgColor
        self.clipsToBounds = true
        self.titleLabel?.numberOfLines = 0
        self.titleLabel?.textAlignment = .center
        self.setTitleColor(.white, for: .normal)
    }
}


// MARK: - UIbutton extenstion
extension UIButton{
    func addTextSpacing(spacing: CGFloat = 4.5, fontcolor: UIColor = config.buttonTextColor, fonttype: String = config.fontNameBold , fontsize: CGFloat = config.largeLabelSize){
        
        
        let attributedString = NSMutableAttributedString(string: (self.titleLabel?.text!)!)
        attributedString.addAttribute(NSAttributedString.Key.kern, value: spacing, range: NSRange(location: 0, length: (self.titleLabel?.text!.count)!))
        
        attributedString.addAttribute(NSAttributedString.Key.font, value: UIFont(name: fonttype, size: fontsize) as Any, range: NSRange(location: 0, length: (self.titleLabel?.text!.count)!))
        
        attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: fontcolor, range: NSRange(location: 0, length: (self.titleLabel?.text!.count)!))
        
        self.setAttributedTitle(attributedString, for: .normal)
        
    }
}


extension UILabel{
    func addTextSpacing(spacing: CGFloat){
        let attributedString = NSMutableAttributedString(string: self.text ?? "")
        attributedString.addAttribute(NSAttributedString.Key.kern, value: spacing, range: NSRange(location: 0, length: self.text?.count ?? 0))
        self.attributedText = attributedString
    }
}


