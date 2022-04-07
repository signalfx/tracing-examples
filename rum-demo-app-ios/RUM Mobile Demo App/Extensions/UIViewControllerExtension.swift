//
//  UIViewControllerExtension.swift
//  RUM Mobile Demo App
//
//  Created by Shraddha Hattimare on 30/09/21.
//

import Foundation
import UIKit

let window = UIApplication.shared.keyWindow

let topPadding = window?.safeAreaInsets.top

let bottomPadding = window?.safeAreaInsets.bottom

let guide = window?.safeAreaLayoutGuide

let height = guide?.layoutFrame.size.height

let safeAreaWidth = guide?.layoutFrame.size.width



extension UIViewController {
    
    func hideKeyboradIfShown(){
        view.endEditing(true)
    }
    
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    
    func setupNavBar(title: String){
        
        self.navigationController?.navigationBar.backgroundColor = config.viewBlueBackground
        self.navigationController?.hidesBarsOnSwipe = false
        
        guard let menuImage = config.settingsIcon, let logoImage = config.navigationTitlelogo else { return }
        
        let menu = UIBarButtonItem.init(image: menuImage, style: .plain, target: self, action: nil)
        
        
        let space = UIBarButtonItem(barButtonSystemItem: UIBarButtonItem.SystemItem.fixedSpace, target: nil, action: nil)
        space.width = 16.0
        
        navigationItem.setRightBarButtonItems([menu, space], animated: true)
        
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
        self.navigationItem.leftBarButtonItem = UIBarButtonItem(image: logoImage, style: .plain, target: nil, action: nil)
        self.navigationItem.title = title
        self.navigationController?.navigationBar.tintColor = config.secondaryAccentColor
        self.navigationController?.navigationBar.titleTextAttributes =
        [NSAttributedString.Key.foregroundColor: config.secondaryAccentColor,
         NSAttributedString.Key.font: UIFont(name: config.fontNameRegular, size: config.subTitleMidFontSize)!]
        
        let headerView: UIView = {
            let vi = UIView()
            vi.translatesAutoresizingMaskIntoConstraints = false
            vi.backgroundColor = config.viewBlueBackground
            return vi
        }()
        
        view.addSubview(headerView)
        
        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: view.topAnchor),
            headerView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 0),
            headerView.rightAnchor.constraint(equalTo: view.rightAnchor),
            headerView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 75),
        ])
        
        
        
    }
    
    func setupNavBarWithoutTools(title: String) {
        
        self.navigationItem.title = ""
        self.navigationController?.hidesBarsOnSwipe = true
        
        let headerView: UIView = {
            let vi = UIView()
            vi.translatesAutoresizingMaskIntoConstraints = false
            vi.backgroundColor = config.viewBlueBackground
            return vi
        }()
        
        let headerTitle: UILabel = {
            let lab = UILabel()
            lab.translatesAutoresizingMaskIntoConstraints = false
            lab.font = UIFont(name: config.fontNameRegular, size: config.mainTitleFontSize)
            lab.textColor = config.secondaryAccentColor
            lab.adjustsFontForContentSizeCategory = true
            lab.minimumScaleFactor = 0.5
            lab.numberOfLines = 1
            lab.textAlignment = .left
            lab.text = title
            return lab
        }()
        
        
        view.addSubview(headerView)
        view.addSubview(headerTitle)
        
        NSLayoutConstraint.activate([
            headerView.topAnchor.constraint(equalTo: view.topAnchor),
            headerView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 0),
            headerView.rightAnchor.constraint(equalTo: view.rightAnchor),
            headerView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 60),
            
            headerTitle.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            headerTitle.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            
            headerTitle.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
            headerTitle.heightAnchor.constraint(equalToConstant: 25),
        ])
        
    }
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
    
    
    func setupRemainingNavItems() {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        navigationController?.navigationBar.setBackgroundImage(UIImage(), for: UIBarMetrics.default)
        navigationController?.navigationBar.shadowImage = UIImage()
        
        let titleImageView = UIImageView(image: config.navigationTitlelogo)
        titleImageView.frame = CGRect(x: 0, y: 0, width: 30, height: 30)
        titleImageView.contentMode = .scaleAspectFit
        titleImageView.clipsToBounds = true
        navigationItem.titleView = titleImageView
        
        let button = UIButton(type: .custom)
        button.setImage(UIImage (named: "chevron-left"), for: .normal)
        button.frame = CGRect(x: 0.0, y: 0.0, width: 30.0, height: 30.0)
        button.addTarget(self, action: #selector(gotoPreviousScreen), for: .touchUpInside)
        
        
        let barButtonItem = UIBarButtonItem(customView: button)
        
        self.navigationItem.leftBarButtonItem = barButtonItem
        
        let button1 = UIButton(type: .custom)
        button1.setImage(UIImage (named: "chevron-left"), for: .normal)
        button1.frame = CGRect(x: 0.0, y: 0.0, width: 30.0, height: 30.0)
        button1.addTarget(self, action: #selector(gotoPreviousScreen), for: .touchUpInside)
        
        
        let barButtonItem1 = UIBarButtonItem(customView: button1)
        
        self.navigationItem.rightBarButtonItem = barButtonItem1
    }
    
    
    @objc private func handlePopViewController() {
        self.navigationController?.popViewController(animated: true)
    }
    
    @objc private func handleDismissVCWithoutNotification() {
        self.dismiss(animated: true) {
            return
        }
    }
    
    @objc func handleDismissKeyboard() {
        for textField in self.view.subviews where textField is UITextField {
            textField.resignFirstResponder()
        }
    }
    
    @objc func handleDismissSettings() {
        self.dismiss(animated: false, completion: nil)
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
    
    func showMenuOption() {
        
        DispatchQueue.main.async {
            let viewMainContainer = UIView.init(frame: window?.bounds ?? .zero)
            viewMainContainer.backgroundColor = UIColor.black.withAlphaComponent(0.3)
            let viewForMenu = UIView()
            let lblTitle = UILabel.init(frame: CGRect.init(x: 0, y: 0, width: 150, height: 45))
            lblTitle.text = "Change Config URL"
            lblTitle.textAlignment = .center
            lblTitle.textColor = .black
            lblTitle.font = UIFont.systemFont(ofSize: 14)
            lblTitle.backgroundColor = .white
            viewForMenu.addSubview(lblTitle)
            viewForMenu.frame.origin.x = UIScreen.main.bounds.width - (lblTitle.frame.width + 20)
            viewForMenu.frame.origin.y = (window?.safeAreaInsets.top ?? 0) + 15
            viewForMenu.frame.size.height = 45
            viewForMenu.backgroundColor = .white
            
            
            
    //        let tapGesture = UITapGestureRecognizer.init(target: self, action: #selector(changeConfigURLButtonClicked(_:)))
    //        tapGesture.delegate = self
    //        viewForMenu.addGestureRecognizer(tapGesture)
    //
    //        let tapGestureOfMainView = UITapGestureRecognizer.init(target: self, action: #selector(tappedOutsideView(_:)))
    //        tapGestureOfMainView.delegate = self
    //        viewMainContainer.addGestureRecognizer(tapGestureOfMainView)
            
            let btnForDismiss = UIButton.init(frame: viewMainContainer.frame)
            viewMainContainer.addSubview(btnForDismiss)
            btnForDismiss.addTarget(self, action: #selector(self.tappedOutsideView(_:)), for: .touchUpInside)
            btnForDismiss.tag = 112010
            btnForDismiss.isUserInteractionEnabled = true
            
            viewMainContainer.addSubview(viewForMenu)
            
            let btn = UIButton.init(frame: viewForMenu.frame)
            viewMainContainer.addSubview(btn)
            btn.backgroundColor = UIColor.red.withAlphaComponent(0.5)
            btn.addTarget(self, action: #selector(self.changeConfigURLButtonClicked(_:)), for: .touchUpInside)
            btn.isUserInteractionEnabled = true
            btn.isAccessibilityElement = true
            btn.accessibilityLabel = "header_context_menu_item"
            
            viewMainContainer.tag = 102010
    //        viewMainContainer.bringSubviewToFront(viewForMenu)
            
            window?.addSubview(viewMainContainer)
        }
    }
    
    @objc func changeConfigURLButtonClicked(_ sender : UIButton) {
        print("CLICKED BUTTON ++ ==>")
    }
    
    @objc func tappedOutsideView(_ sender : UIButton) {
        print("CLICKED BUTTON ==>>")// \(sender.tag)")
        if let viewAppeared = window?.viewWithTag(102010) {
            viewAppeared.removeFromSuperview()
        }
    }
    
    func startSpinner() {
        DispatchQueue.main.async {
            let spinnerView = ActivityIndicatorView()
            spinnerView.tag = 0xABCD1611
            spinnerView.translatesAutoresizingMaskIntoConstraints = false
            
            self.view.addSubview(spinnerView)
            
            NSLayoutConstraint.activate([
                spinnerView.leftAnchor.constraint(equalTo: self.view.leftAnchor),
                spinnerView.rightAnchor.constraint(equalTo: self.view.rightAnchor),
                spinnerView.topAnchor.constraint(equalTo: self.view.topAnchor),
                spinnerView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
            ])
            spinnerView.spinner.startAnimating()
            
            guard let nav = self.navigationController?.navigationBar else { return }
            nav.isHidden = true
            guard let tab = self.tabBarController?.tabBar else { return }
            tab.isHidden = true
        }
    }
    
    func stopSpinner() {
        DispatchQueue.main.async {
            if let spinView = self.view.viewWithTag(0xABCD1611) {
                spinView.removeFromSuperview()
            }
        }
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
    
    func absPoint(_ view: UIView?) -> CGPoint
    {
        var ret = CGPoint(x: view?.frame.origin.x ?? 0.0, y: view?.frame.origin.y ?? 0.0)
        
        if view?.superview != nil {
            let addPoint = absPoint(view?.superview)
            ret = CGPoint(x: ret.x + addPoint.x, y: ret.y + addPoint.y)
        }
        return ret
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

//MARK:- Image button , image at right side
class ButtonWithImageAtRight: UIButton {
    
    override func layoutSubviews() {
        super.layoutSubviews()
        if imageView != nil {
            imageEdgeInsets = UIEdgeInsets(top: 5, left: 0, bottom: 5, right: -(bounds.width - 35))
            titleEdgeInsets = UIEdgeInsets(top: 0, left: -(imageView?.frame.width)!, bottom: 0, right: 0)
        }
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


extension UITextField{
    func addPlaceholderSpacing(spacing: CGFloat){
        let attributedString = NSMutableAttributedString(string: self.placeholder ?? "")
        attributedString.addAttribute(NSAttributedString.Key.kern, value: spacing, range: NSRange(location: 0, length: self.placeholder?.count ?? 0))
        self.attributedPlaceholder = attributedString
    }
}




extension UIApplication
{
    class func topViewController(base: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController?
    {
        if let nav = base as? UINavigationController
        {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController
        {
            if let selected = tab.selectedViewController
            {
                return topViewController(base: selected)
            }
        }
        if let presented = base?.presentedViewController
        {
            return topViewController(base: presented)
        }
        return base
    }
}

class ActivityIndicatorView: UIView {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = config.accentColor
        alpha = 0.9
        setupView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented".localized())
    }
    
    lazy var spinner: UIActivityIndicatorView = {
        let ai = UIActivityIndicatorView()
        ai.translatesAutoresizingMaskIntoConstraints = false
        ai.hidesWhenStopped = true
        if #available(iOS 13.0, *) {
            ai.style = UIActivityIndicatorView.Style.large
        } else {
            // Fallback on earlier versions
            ai.style = UIActivityIndicatorView.Style.whiteLarge
            
        }
        return ai
    }()
    
    func setupView() {
        addSubview(spinner)
        NSLayoutConstraint.activate([
            spinner.centerYAnchor.constraint(equalTo: centerYAnchor),
            spinner.centerXAnchor.constraint(equalTo: centerXAnchor),
        ])
    }
}

