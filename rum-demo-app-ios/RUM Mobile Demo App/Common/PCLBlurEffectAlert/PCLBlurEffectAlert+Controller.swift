//
//  PCLBlurEffectAlert+Controller.swift
//  PCLBlurEffectAlert
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit

public typealias PCLBlurEffectAlertController = PCLBlurEffectAlert.Controller

extension PCLBlurEffectAlert {
    open class Controller: UIViewController {
        // Property
        fileprivate var isNeedlayout = true
        fileprivate var message: String?
        fileprivate var textField: UITextField?
        fileprivate var imageView: UIImageView?
        var style: PCLBlurEffectAlert.ControllerStyle = .actionSheet
        fileprivate var effect: UIBlurEffect = UIBlurEffect(style: .extraLight)
        
        // Actions
        open fileprivate(set) var actions: [PCLBlurEffectAlertAction] = []
        fileprivate var cancelAction: PCLBlurEffectAlertAction?
        fileprivate var cancelActionTag: Int?
        fileprivate var keyboardHeight: CGFloat = 0
        
        // TextFields
        fileprivate var textFields: [UITextField] = []
        
        // Getter
        fileprivate var isActionSheet: Bool { return style == .actionSheet }
        fileprivate var isAlert: Bool { return style == .alert }
        fileprivate var isAlertVertical: Bool { return style == .alertVertical }
        fileprivate var hasTitle: Bool { return title?.isEmpty == false }
        fileprivate var hasMessage: Bool { return message?.isEmpty == false }
        fileprivate var hasImageView: Bool { return imageView != nil }
        fileprivate var hasTextField: Bool { return !textFields.isEmpty }
        
        // OverlayView
        let overlayView = UIView()
        fileprivate let tapGestureRecognizer = UITapGestureRecognizer()
        // ContainerView
        let containerView = RespondsView()
        fileprivate var containerViewBottomLayoutConstraint: NSLayoutConstraint!
        // AlertView
        let alertView = UIView()
        fileprivate var alertViewWidthConstraint: NSLayoutConstraint?
        fileprivate var alertViewHeightConstraint: NSLayoutConstraint?
        // CornerView
        let cornerView = UIView()
        fileprivate var cornerViewHeightConstraint: NSLayoutConstraint!
        // textAreaView
        fileprivate let textAreaView = UIView()
        fileprivate var textAreaHeight: CGFloat = 0
        fileprivate var textAreaViewHeightConstraint: NSLayoutConstraint?
        fileprivate var textAreaVisualEffectView: UIVisualEffectView!
        fileprivate var textAreaVisualEffectViewHeightConstraint: NSLayoutConstraint?
        fileprivate let textAreaBackgroundView = UIView()
        fileprivate var textAreaBackgroundViewHeightConstraint: NSLayoutConstraint?
        // titleLabel
        fileprivate let titleLabel = UILabel()
        // messageLabel
        fileprivate let messageLabel = UILabel()
        
        // Customize
        fileprivate var alertViewWidth: CGFloat = 0
        fileprivate var cornerRadius: CGFloat = 0
        fileprivate var thin: CGFloat = 1 / UIScreen.main.scale
        fileprivate var margin: CGFloat = 8
        fileprivate var overlayBackgroundColor: UIColor = UIColor.black.withAlphaComponent(0.3)
        fileprivate var backgroundColor: UIColor = .clear
        fileprivate var buttonBackgroundColor: UIColor = .clear
        fileprivate var textFieldsViewBackgroundColor: UIColor = UIColor.white.withAlphaComponent(0.1)
        fileprivate var titleFont: UIFont = .boldSystemFont(ofSize: 16)
        fileprivate var titleColor: UIColor = .black
        fileprivate var messageFont: UIFont = .systemFont(ofSize: 14)
        fileprivate var messageColor: UIColor = .black
        fileprivate var buttonFont: [PCLBlurEffectAlert.ActionStyle : UIFont] = [
            .default: UIFont.systemFont(ofSize: 16),
            .cancel: UIFont.systemFont(ofSize: 16),
            .destructive: UIFont.systemFont(ofSize: 16)
        ]
        fileprivate var buttonTextColor: [PCLBlurEffectAlert.ActionStyle : UIColor] = [
            .default: .black,
            .cancel: .black,
            .destructive: .red
        ]
        fileprivate var buttonDisableTextColor: [PCLBlurEffectAlert.ActionStyle : UIColor] = [
            .default: .gray,
            .cancel: .gray,
            .destructive: .gray
        ]
        fileprivate var textFieldHeight: CGFloat = 32
        fileprivate var textFieldBorderColor = UIColor.black.withAlphaComponent(0.15)
        fileprivate var buttonHeight: CGFloat = 44
        
        private var tintColor: UIColor {
            return view.tintColor
        }
        
        open override var prefersStatusBarHidden: Bool {
            return presentingViewController?.prefersStatusBarHidden ?? super.prefersStatusBarHidden
        }
        
        public convenience init(title: String?,
                                message: String?,
                                effect: UIBlurEffect = UIBlurEffect(style: .extraLight),
                                style: PCLBlurEffectAlert.ControllerStyle) {
            self.init(nibName: nil, bundle: nil)
            self.title = title
            self.message = message
            self.style = style
            self.effect = effect
            self.textAreaVisualEffectView = UIVisualEffectView(effect: effect) as UIVisualEffectView
            
            // NotificationCenter
            PCLBlurEffectAlert.NotificationManager.shared.addAlertActionEnabledDidChangeNotificationObserver(self)
            PCLBlurEffectAlert.NotificationManager.shared.addKeyboardNotificationObserver(self)
            
            modalPresentationStyle = .overCurrentContext
            transitioningDelegate = self
            view.frame.size = UIScreen.main.bounds.size
            overlayView.frame = UIScreen.main.bounds
            view.insertSubview(overlayView, at: 0)
            view.addSubview(containerView)
            
            containerView.delegate = self
            containerView.addSubview(alertView)
            
            cornerView.addSubview(textAreaBackgroundView)
            cornerView.addSubview(textAreaVisualEffectView)
            cornerView.addSubview(textAreaView)
            
            alertView.addSubview(cornerView)
            switch style {
            case .actionSheet:
                alertViewWidth = UIScreen.main.bounds.width - (margin * 2)
            default:
                alertViewWidth = 320 - (margin * 2)
            }
            cornerRadius = 4
            buttonTextColor[.default] = tintColor
            buttonTextColor[.cancel] = tintColor
            DispatchQueue.main.async {
                self.configureConstraints()
            }
        }
        deinit {
            PCLBlurEffectAlert.NotificationManager.shared.removeAlertActionEnabledDidChangeNotificationObserver(self)
            PCLBlurEffectAlert.NotificationManager.shared.removeKeyboardNotificationObserver(self)
        }
        // layout
        open override func viewWillAppear(_ animated: Bool) {
            super.viewWillAppear(animated)
            adjustLayout()
        }
    }
}

// MARK: - User Setting
extension PCLBlurEffectAlertController {
    //    ActionSheet: var alertViewWidth: CGFloat = UIScreen.main.bounds.width - (margin * 2)
    //    Alert: var alertViewWidth = 320 - (margin * 2)
    open func configure(alertViewWidth: CGFloat) {
        self.alertViewWidth = alertViewWidth
    }
    //    var cornerRadius: CGFloat = 0
    open func configure(cornerRadius: CGFloat) {
        self.cornerRadius = cornerRadius
    }
    //    var thin: CGFloat = 1 / UIScreen.main.scale
    open func configure(thin: CGFloat) {
        self.thin = thin
    }
    //    var margin: CGFloat = 8
    open func configure(margin: CGFloat) {
        self.margin = margin
    }
    //    var backgroundColor = .clear
    open func configure(backgroundColor: UIColor) {
        self.backgroundColor = backgroundColor
    }
    //    var buttonBackgroundColor = .clear
    open func configure(buttonBackgroundColor: UIColor) {
        self.buttonBackgroundColor = buttonBackgroundColor
    }
    //    var overlayBackgroundColor = UIColor.black.withAlphaComponent(0.3)
    open func configure(overlayBackgroundColor: UIColor) {
        self.overlayBackgroundColor = overlayBackgroundColor
    }
    //    var textFieldsViewBackgroundColor = UIColor.white.withAlphaComponent(0.1)
    open func configure(textFieldsViewBackgroundColor: UIColor) {
        self.textFieldsViewBackgroundColor = textFieldsViewBackgroundColor
    }
    //    var titleFont = UIFont.boldSystemFont(ofSize: 16)
    //    var titleColor: UIColor = .brown
    open func configure(titleFont: UIFont) {
        self.titleFont = titleFont
    }
    open func configure(titleColor: UIColor) {
        self.titleColor = titleColor
    }
    open func configure(titleFont: UIFont, titleColor: UIColor) {
        self.titleFont = titleFont
        self.titleColor = titleColor
    }
    //    var messageFont = UIFont.systemFont(ofSize: 14)
    //    var messageColor: UIColor = .black
    open func configure(messageFont: UIFont) {
        self.messageFont = messageFont
    }
    open func configure(messageColor: UIColor) {
        self.messageColor = messageColor
    }
    open func configure(messageFont: UIFont, messageColor: UIColor) {
        self.messageFont = messageFont
        self.messageColor = messageColor
    }
    //    var buttonFont: [PCLBlurEffectAlert.ActionStyle : UIFont] = [
    //        .default: UIFont.systemFont(ofSize: 16),
    //        .cancel: UIFont.systemFont(ofSize: 16),
    //        .destructive: UIFont.systemFont(ofSize: 16)
    //    ]
    //    var buttonTextColor: [PCLBlurEffectAlert.ActionStyle : UIColor] = [
    //        .default: .black,
    //        .cancel: .gray,
    //        .destructive: .red
    //    ]
    //    var buttonDisableTextColor: [PCLBlurEffectAlert.ActionStyle : UIColor] = [
    //        .default: .black,
    //        .cancel: .black,
    //        .destructive: .red
    //    ]
    open func configure(buttonFont font: [PCLBlurEffectAlert.ActionStyle : UIFont]? = nil,
                        buttonTextColor textColor: [PCLBlurEffectAlert.ActionStyle : UIColor]? = nil,
                        buttonDisableTextColor disableTextColor: [PCLBlurEffectAlert.ActionStyle : UIColor]? = nil) {
        if let font = font?[.default] {
            self.buttonFont[.default] = font
        }
        if let font = font?[.destructive] {
            self.buttonFont[.destructive] = font
        }
        if let font = font?[.cancel] {
            self.buttonFont[.cancel] = font
        }
        if let textColor = textColor?[.default] {
            self.buttonTextColor[.default] = textColor
        }
        if let textColor = textColor?[.destructive] {
            self.buttonTextColor[.destructive] = textColor
        }
        if let textColor = textColor?[.cancel] {
            self.buttonTextColor[.cancel] = textColor
        }
        if let textColor = disableTextColor?[.default] {
            self.buttonDisableTextColor[.default] = textColor
        }
        if let textColor = disableTextColor?[.destructive] {
            self.buttonDisableTextColor[.destructive] = textColor
        }
        if let textColor = disableTextColor?[.cancel] {
            self.buttonDisableTextColor[.cancel] = textColor
        }
    }
    //    var textFieldHeight: CGFloat = 32
    open func configure(textFieldHeight: CGFloat) {
        self.textFieldHeight = textFieldHeight
    }
    //    var textFieldBorderColor = UIColor.black.withAlphaComponent(0.15)
    open func configure(textFieldBorderColor: UIColor) {
        self.textFieldBorderColor = textFieldBorderColor
    }
    //    var buttonHeight: CGFloat = 44
    open func configure(buttonHeight: CGFloat) {
        self.buttonHeight = buttonHeight
    }
    // Adds Action
    open func addAction(_ action: PCLBlurEffectAlertAction) {
        // Error
        if action.style.isCancel && actions.filter({ $0.style.isCancel }).count > 0 {
            fatalError("Can not be used plurality cancel button")
        }
        action.tag = actions.count
        action.button?.tag = action.tag
        if action.style.isCancel {
            cancelAction = action
            cancelActionTag = action.tag
        }
        actions.append(action)
        action.button?.setTitle(action.title, for: .normal)
        action.button?.isEnabled = action.isEnabled
        action.visualEffectView = UIVisualEffectView(effect: effect) as UIVisualEffectView
        action.visualEffectView?.isUserInteractionEnabled = false
    }
    // Adds TextFields
    open func addTextField(with configurationHandler: ((UITextField?) -> Void)? = nil) {
        let textField = UITextField()
        textField.backgroundColor = .clear
        configurationHandler?(textField)
        textFields.append(textField)
    }
    // Adds ImageView
    open func addImageView(with image: UIImage, configurationHandler: ((UIImageView?) -> Void)? = nil) {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFill
        imageView.clipsToBounds = true
        imageView.backgroundColor = .clear
        imageView.image = image
        configurationHandler?(imageView)
        self.imageView = imageView
    }
    // show
    open func show()
    {
        DispatchQueue.main.async {
            UIApplication.shared.topViewController?.present(self, animated: false, completion: nil)
        }
        
//        if UIApplication.shared.applicationState == .background || UIApplication.shared.applicationState == .inactive
//        {
//            DispatchQueue.main.async {
//                UIApplication.shared.topViewController?.present(self, animated: false, completion: nil)
//            }
//        }else
//        {
//            DispatchQueue.main.async {
//                UIApplication.shared.topViewController?.present(self, animated: true, completion: nil)
//            }
//        }
    }
}

// MARK: - Private
private extension PCLBlurEffectAlertController {
    func configureConstraints() {
        
        DispatchQueue.main.async {
            self.configureContainerViewConstraints()
            self.configureAlertViewConstraints()
            self.configureCornerViewConstraints()
            self.configureTextAreaViewConstraints()
        }
    }
    func configureContainerViewConstraints() {
        containerView.translatesAutoresizingMaskIntoConstraints = false
        let topConstraint = NSLayoutConstraint(item: containerView,
                                               attribute: .top,
                                               relatedBy: .equal,
                                               toItem: view,
                                               attribute: .top,
                                               multiplier: 1,
                                               constant: 0)
        let rightConstraint = NSLayoutConstraint(item: containerView,
                                                 attribute: .right,
                                                 relatedBy: .equal,
                                                 toItem: view,
                                                 attribute: .right,
                                                 multiplier: 1,
                                                 constant: 0)
        let leftConstraint = NSLayoutConstraint(item: containerView,
                                                attribute: .left,
                                                relatedBy: .equal,
                                                toItem: view,
                                                attribute: .left,
                                                multiplier: 1,
                                                constant: 0)
        containerViewBottomLayoutConstraint = NSLayoutConstraint(item: containerView,
                                                                 attribute: .bottom,
                                                                 relatedBy: .equal,
                                                                 toItem: view,
                                                                 attribute: .bottom,
                                                                 multiplier: 1,
                                                                 constant: 0)
        DispatchQueue.main.async {
            self.view.addConstraints([topConstraint,
                                 rightConstraint,
                                 leftConstraint,
                                      self.containerViewBottomLayoutConstraint])
        }
    }
    func configureAlertViewConstraints() {
        alertView.translatesAutoresizingMaskIntoConstraints = false
        switch style {
        case .actionSheet:
            let centerXConstraint = NSLayoutConstraint(item: alertView,
                                                       attribute: .centerX,
                                                       relatedBy: .equal,
                                                       toItem: containerView,
                                                       attribute: .centerX,
                                                       multiplier: 1,
                                                       constant: 0)
            let bottomConstraint = NSLayoutConstraint(item: alertView,
                                                      attribute: .bottom,
                                                      relatedBy: .equal,
                                                      toItem: containerView,
                                                      attribute: .bottom,
                                                      multiplier: 1,
                                                      constant: -(margin))
            alertViewWidthConstraint = NSLayoutConstraint(item: alertView,
                                                          attribute: .width,
                                                          relatedBy: .equal,
                                                          toItem: nil,
                                                          attribute: .width,
                                                          multiplier: 1,
                                                          constant: alertViewWidth)
            alertViewHeightConstraint = NSLayoutConstraint(item: alertView,
                                                           attribute: .height,
                                                           relatedBy: .equal,
                                                           toItem: nil,
                                                           attribute: .height,
                                                           multiplier: 1,
                                                           constant: 0)
            
            DispatchQueue.main.async {
                if let widthConstraint = self.alertViewWidthConstraint, let heightConstraint = self.alertViewHeightConstraint {
                    self.containerView.addConstraints([centerXConstraint,
                                                  bottomConstraint,
                                                  widthConstraint,
                                                  heightConstraint])
                }
            }
        default:
            let centerXConstraint = NSLayoutConstraint(item: alertView, attribute: .centerX, relatedBy: .equal, toItem: containerView, attribute: .centerX, multiplier: 1, constant: 0)
            let centerYConstraint = NSLayoutConstraint(item: alertView, attribute: .centerY, relatedBy: .equal, toItem: containerView, attribute: .centerY, multiplier: 1, constant: 0)
            alertViewWidthConstraint = NSLayoutConstraint(item: alertView, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .width, multiplier: 1, constant: alertViewWidth)
            alertViewHeightConstraint = NSLayoutConstraint(item: alertView, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .height, multiplier: 1, constant: 0)
            
            if let widthConstraint = self.alertViewWidthConstraint, let heightConstraint = self.alertViewHeightConstraint {
                DispatchQueue.main.async {
                    self.containerView.addConstraints([centerXConstraint,
                                                  centerYConstraint,
                                                       widthConstraint,
                                                       heightConstraint])
                }
            }
        }
    }
    func configureCornerViewConstraints() {
        cornerView.translatesAutoresizingMaskIntoConstraints = false
        switch style {
        case .actionSheet:
            let topConstraint = NSLayoutConstraint(item: cornerView,
                                                   attribute: .top,
                                                   relatedBy: .equal,
                                                   toItem: alertView,
                                                   attribute: .top,
                                                   multiplier: 1,
                                                   constant: 0)
            let rightConstraint = NSLayoutConstraint(item: cornerView,
                                                     attribute: .right,
                                                     relatedBy: .equal,
                                                     toItem: alertView,
                                                     attribute: .right,
                                                     multiplier: 1,
                                                     constant: 0)
            let leftConstraint = NSLayoutConstraint(item: cornerView,
                                                    attribute: .left,
                                                    relatedBy: .equal,
                                                    toItem: alertView,
                                                    attribute: .left,
                                                    multiplier: 1,
                                                    constant: 0)
            cornerViewHeightConstraint = NSLayoutConstraint(item: cornerView,
                                                            attribute: .height,
                                                            relatedBy: .equal,
                                                            toItem: nil,
                                                            attribute: .height,
                                                            multiplier: 1,
                                                            constant: 0)
            DispatchQueue.main.async {
                guard let cornerViewHeightConstraint = self.cornerViewHeightConstraint else {return}
                self.alertView.addConstraints([topConstraint, rightConstraint, leftConstraint, cornerViewHeightConstraint])
            }
        default:
            let topConstraint = NSLayoutConstraint(item: cornerView,
                                                   attribute: .top,
                                                   relatedBy: .equal,
                                                   toItem: alertView,
                                                   attribute: .top,
                                                   multiplier: 1,
                                                   constant: 0)
            let rightConstraint = NSLayoutConstraint(item: cornerView,
                                                     attribute: .right,
                                                     relatedBy: .equal,
                                                     toItem: alertView,
                                                     attribute: .right,
                                                     multiplier: 1,
                                                     constant: 0)
            let leftConstraint = NSLayoutConstraint(item: cornerView,
                                                    attribute: .left,
                                                    relatedBy: .equal,
                                                    toItem: alertView,
                                                    attribute: .left,
                                                    multiplier: 1,
                                                    constant: 0)
            let bottomLayoutConstraint = NSLayoutConstraint(item: cornerView,
                                                            attribute: .bottom,
                                                            relatedBy: .equal,
                                                            toItem: alertView,
                                                            attribute: .bottom,
                                                            multiplier: 1,
                                                            constant: 0)
            DispatchQueue.main.async {
                self.alertView.addConstraints([topConstraint,
                                          rightConstraint,
                                          leftConstraint,
                                          bottomLayoutConstraint])
            }
        }
    }
    func configureTextAreaViewConstraints() {
        textAreaView.translatesAutoresizingMaskIntoConstraints = false
        textAreaVisualEffectView.translatesAutoresizingMaskIntoConstraints = false
        textAreaBackgroundView.translatesAutoresizingMaskIntoConstraints = false
        
        let textAreaViewTopConstraint = NSLayoutConstraint(item: textAreaView,
                                                           attribute: .top,
                                                           relatedBy: .equal,
                                                           toItem: cornerView,
                                                           attribute: .top,
                                                           multiplier: 1,
                                                           constant: 0)
        let textAreaViewRightConstraint = NSLayoutConstraint(item: textAreaView,
                                                             attribute: .right,
                                                             relatedBy: .equal,
                                                             toItem: cornerView,
                                                             attribute: .right,
                                                             multiplier: 1,
                                                             constant: 0)
        let textAreaViewLeftConstraint = NSLayoutConstraint(item: textAreaView,
                                                            attribute: .left,
                                                            relatedBy: .equal,
                                                            toItem: cornerView,
                                                            attribute: .left,
                                                            multiplier: 1,
                                                            constant: 0)
        textAreaViewHeightConstraint = NSLayoutConstraint(item: textAreaView,
                                                          attribute: .height,
                                                          relatedBy: .equal,
                                                          toItem: nil,
                                                          attribute: .height,
                                                          multiplier: 1,
                                                          constant: 0)
        DispatchQueue.main.async {
            if let textAreaWidth = self.textAreaViewHeightConstraint {
                self.cornerView.addConstraints([textAreaViewTopConstraint,
                                           textAreaViewRightConstraint,
                                           textAreaViewLeftConstraint,
                                                textAreaWidth])
            }
        }
        
        if let visualView = self.textAreaVisualEffectView
        {
            let textAreaVisualEffectViewTopConstraint = NSLayoutConstraint(item: visualView,
                                                                           attribute: .top,
                                                                           relatedBy: .equal,
                                                                           toItem: cornerView,
                                                                           attribute: .top,
                                                                           multiplier: 1,
                                                                           constant: 0)
            let textAreaVisualEffectViewRightConstraint = NSLayoutConstraint(item: visualView,
                                                                             attribute: .right,
                                                                             relatedBy: .equal,
                                                                             toItem: cornerView,
                                                                             attribute: .right,
                                                                             multiplier: 1,
                                                                             constant: 0)
            let textAreaVisualEffectViewLeftConstraint = NSLayoutConstraint(item: visualView,
                                                                            attribute: .left,
                                                                            relatedBy: .equal,
                                                                            toItem: cornerView,
                                                                            attribute: .left,
                                                                            multiplier: 1,
                                                                            constant: 0)
            textAreaVisualEffectViewHeightConstraint = NSLayoutConstraint(item: visualView,
                                                                          attribute: .height,
                                                                          relatedBy: .equal,
                                                                          toItem: nil,
                                                                          attribute: .height,
                                                                          multiplier: 1,
                                                                          constant: 0)
            DispatchQueue.main.async {
                if let textAreaVisualEffectViewHeightConstraint = self.textAreaVisualEffectViewHeightConstraint {
                    self.cornerView.addConstraints([textAreaVisualEffectViewTopConstraint,
                                               textAreaVisualEffectViewRightConstraint,
                                               textAreaVisualEffectViewLeftConstraint,
                                               textAreaVisualEffectViewHeightConstraint])
                }
                    
            }
        }
        
        let textAreaBackgroundViewTopConstraint = NSLayoutConstraint(item: textAreaBackgroundView,
                                                                     attribute: .top,
                                                                     relatedBy: .equal,
                                                                     toItem: cornerView,
                                                                     attribute: .top,
                                                                     multiplier: 1,
                                                                     constant: 0)
        let textAreaBackgroundViewRightConstraint = NSLayoutConstraint(item: textAreaBackgroundView,
                                                                       attribute: .right,
                                                                       relatedBy: .equal,
                                                                       toItem: cornerView,
                                                                       attribute: .right,
                                                                       multiplier: 1,
                                                                       constant: 0)
        let textAreaBackgroundViewLeftConstraint = NSLayoutConstraint(item: textAreaBackgroundView,
                                                                      attribute: .left,
                                                                      relatedBy: .equal,
                                                                      toItem: cornerView,
                                                                      attribute: .left,
                                                                      multiplier: 1,
                                                                      constant: 0)
        textAreaBackgroundViewHeightConstraint = NSLayoutConstraint(item: textAreaBackgroundView,
                                                                    attribute: .height,
                                                                    relatedBy: .equal,
                                                                    toItem: nil,
                                                                    attribute: .height,
                                                                    multiplier: 1,
                                                                    constant: 0)
        DispatchQueue.main.async {
            guard let textAreaBackgroundViewHeightConstraint = self.textAreaBackgroundViewHeightConstraint else {return}
            self.cornerView.addConstraints([textAreaBackgroundViewTopConstraint,
                                       textAreaBackgroundViewRightConstraint,
                                       textAreaBackgroundViewLeftConstraint,
                                       textAreaBackgroundViewHeightConstraint])
        }
    }
    func adjustLayout() {
        guard self.isNeedlayout else { return }
        self.isNeedlayout = false
        self.overlayView.backgroundColor = self.overlayBackgroundColor
        self.alertView.layer.cornerRadius = self.cornerRadius
        self.alertView.clipsToBounds = true
        self.alertViewWidthConstraint?.constant = self.alertViewWidth
        self.cornerView.layer.cornerRadius = self.cornerRadius
        self.cornerView.clipsToBounds = true
        var textAreaPositionY: CGFloat = 0
        textAreaPositionY += (self.margin * 2)
        let textAreaWidth = self.alertViewWidth - (self.margin * 4)
        if self.hasTitle {
            self.titleLabel.frame.size = CGSize(width: textAreaWidth, height: 0)
            self.titleLabel.numberOfLines = 0
            self.titleLabel.textAlignment = .center
            self.titleLabel.font = self.titleFont
            self.titleLabel.textColor = self.titleColor
            self.titleLabel.text = self.title
            self.titleLabel.sizeToFit()
            self.titleLabel.frame = CGRect(x: self.margin * 2,
                                           y: textAreaPositionY,
                                           width: textAreaWidth,
                                           height: self.titleLabel.frame.height)
            self.textAreaView.addSubview(self.titleLabel)
            textAreaPositionY += self.titleLabel.frame.height
        }
        if self.hasMessage {
            if self.hasTitle {
                textAreaPositionY += self.margin
            }
            self.messageLabel.frame.size = CGSize(width: textAreaWidth, height: 0)
            self.messageLabel.numberOfLines = 0
            self.messageLabel.textAlignment = .center
            self.messageLabel.text = self.message
            self.messageLabel.font = self.messageFont
            self.messageLabel.textColor = self.messageColor
            self.messageLabel.sizeToFit()
            self.messageLabel.frame = CGRect(x: self.margin * 2,
                                             y: textAreaPositionY,
                                             width: textAreaWidth,
                                             height: self.messageLabel.frame.height)
            self.textAreaView.addSubview(self.messageLabel)
            textAreaPositionY += self.messageLabel.frame.height
        }
        if let imageView = self.imageView, let image = imageView.image { // hasImageView
            if self.hasTitle || hasMessage {
                textAreaPositionY += margin
            }
            let width = min(alertViewWidth - (margin * 2), image.size.width)
            let height = (width / image.size.width) * image.size.height
            let size = CGSize(width: width, height: height)
            imageView.frame = CGRect(x: (alertViewWidth - size.width) / 2,
                                     y: textAreaPositionY,
                                     width: size.width,
                                     height: size.height)
            textAreaView.addSubview(imageView)
            textAreaPositionY += imageView.frame.height
        }
        if hasTextField {
            if hasTitle || hasMessage || hasImageView {
                textAreaPositionY += margin
            }
            let textFieldsView = UIView()
            textFieldsView.backgroundColor = textFieldsViewBackgroundColor
            textFieldsView.layer.cornerRadius = (cornerRadius / 2)
            textFieldsView.clipsToBounds = true
            let textFieldsViewWidth = textAreaWidth
            var textFieldsViewHeight: CGFloat = 0
            textFieldsView.frame = CGRect(x: margin * 2,
                                          y: textAreaPositionY + margin,
                                          width: textFieldsViewWidth,
                                          height: textFieldsViewHeight)
            textFields.enumerated().forEach { index, textField in
                textField.frame = CGRect(x: margin,
                                         y: CGFloat(index) * textFieldHeight,
                                         width: textFieldsViewWidth - (2 * margin),
                                         height: textFieldHeight)
                textFieldsView.addSubview(textField)
                textFieldsViewHeight += textFieldHeight
                if index > 0 {
                    let topBorder = CALayer()
                    topBorder.frame = CGRect(x: -margin,
                                             y: 0,
                                             width: textFieldsViewWidth + (margin * 2),
                                             height: thin)
                    topBorder.backgroundColor = textFieldBorderColor.cgColor
                    textField.layer.sublayers = [topBorder]
                    textField.clipsToBounds = false
                }
            }
            textFieldsView.layer.borderColor = textFieldBorderColor.cgColor
            textFieldsView.layer.borderWidth = thin
            textAreaView.addSubview(textFieldsView)
            textFieldsView.frame.size.height = textFieldsViewHeight
            textAreaPositionY += textFieldsView.frame.size.height
        }
        if hasTitle || hasMessage || hasImageView || hasTextField {
            textAreaPositionY += (margin * 2)
            textAreaBackgroundView.backgroundColor = backgroundColor
        } else {
            textAreaPositionY = 0
        }
        textAreaHeight = textAreaPositionY
        textAreaViewHeightConstraint?.constant = textAreaHeight
        textAreaVisualEffectViewHeightConstraint?.constant = textAreaHeight
        textAreaBackgroundViewHeightConstraint?.constant = textAreaHeight
        var cornerViewHeight = textAreaHeight
        var alertViewHeight: CGFloat = 0
        // button setUp
        switch style {
        case .alert where actions.count == 2 && (hasTitle || hasMessage || hasImageView || hasTextField):
            cornerViewHeight += thin
            actions.enumerated().forEach { index, action in
                let rect = CGRect(x: CGFloat(index) * alertViewWidth / 2,
                                  y: cornerViewHeight,
                                  width: alertViewWidth / 2,
                                  height: buttonHeight)
                action.backgroundView.frame = rect
                action.backgroundView.backgroundColor = buttonBackgroundColor
                action.visualEffectView?.frame = rect
                action.button.frame = rect
                if let visualEffectView = action.visualEffectView {
                    cornerView.addSubview(action.backgroundView)
                    cornerView.addSubview(visualEffectView)
                }
                action.button.setTitleColor(buttonTextColor[action.style], for: .normal)
                action.button.setTitleColor(buttonDisableTextColor[action.style], for: .disabled)
                action.button.titleLabel?.font = buttonFont[action.style]
                if index == actions.count - 1 {
                    action.visualEffectView?.frame.origin.x += thin
                    action.visualEffectView?.frame.size.width -= thin
                    action.button.frame.origin.x += thin
                    action.button.frame.size.width -= thin
                }
                action.button.addTarget(self,
                                        action: #selector(PCLBlurEffectAlertController.buttonWasTouchUpInside(_:)),
                                        for: .touchUpInside)
                cornerView.addSubview(action.button)
            }
            cornerViewHeight += buttonHeight
        case .alert, .alertVertical:
            actions.enumerated().forEach { index, action in
                cornerViewHeight += thin
                let rect = CGRect(x: 0,
                                  y: cornerViewHeight,
                                  width: alertViewWidth,
                                  height: buttonHeight)
                action.backgroundView.frame = rect
                action.backgroundView.backgroundColor = buttonBackgroundColor
                action.visualEffectView?.frame = rect
                action.button.frame = rect
                if let visualEffectView = action.visualEffectView {
                    cornerView.addSubview(action.backgroundView)
                    cornerView.addSubview(visualEffectView)
                }
                action.button.setTitleColor(buttonTextColor[action.style], for: .normal)
                action.button.setTitleColor(buttonDisableTextColor[action.style], for: .disabled)
                action.button.titleLabel?.font = buttonFont[action.style]
                action.button.addTarget(self,
                                        action: #selector(PCLBlurEffectAlertController.buttonWasTouchUpInside(_:)),
                                        for: .touchUpInside)
                cornerView.addSubview(action.button)
                cornerViewHeight += buttonHeight
            }
        default:
            var cancelIndex = -1
            actions.enumerated().forEach { index, action in
                switch action.style {
                case .cancel:
                    cancelIndex = index
                default:
                    cornerViewHeight += thin
                    let rect = CGRect(x: 0,
                                      y: cornerViewHeight,
                                      width: alertViewWidth,
                                      height: buttonHeight)
                    action.backgroundView.frame = rect
                    action.backgroundView.backgroundColor = buttonBackgroundColor
                    action.visualEffectView?.frame = rect
                    action.button.frame = rect
                    if let visualEffectView = action.visualEffectView {
                        cornerView.addSubview(action.backgroundView)
                        cornerView.addSubview(visualEffectView)
                    }
                    action.button.setTitleColor(buttonTextColor[action.style], for: .normal)
                    action.button.setTitleColor(buttonDisableTextColor[action.style], for: .disabled)
                    action.button.titleLabel?.font = buttonFont[action.style]
                    action.button.addTarget(self,
                                            action: #selector(PCLBlurEffectAlertController.buttonWasTouchUpInside(_:)),
                                            for: .touchUpInside)
                    cornerView.addSubview(action.button)
                    cornerViewHeight += buttonHeight
                }
            }
            alertViewHeight = cornerViewHeight
            if cancelIndex >= 0 { // cancel
                alertViewHeight += margin
                let action = actions[cancelIndex]
                let rect = CGRect(x: 0,
                                  y: alertViewHeight,
                                  width: alertViewWidth,
                                  height: buttonHeight)
                action.backgroundView.frame = rect
                action.backgroundView.backgroundColor = buttonBackgroundColor
                action.visualEffectView?.frame = rect
                action.button.frame = rect
                if let visualEffectView = action.visualEffectView {
                    alertView.addSubview(action.backgroundView)
                    alertView.addSubview(visualEffectView)
                }
                action.button.setTitleColor(buttonTextColor[action.style], for: .normal)
                action.button.setTitleColor(buttonDisableTextColor[action.style], for: .disabled)
                action.button.titleLabel?.font = buttonFont[action.style]
                action.button.addTarget(self,
                                        action: #selector(PCLBlurEffectAlertController.buttonWasTouchUpInside(_:)),
                                        for: .touchUpInside)
                action.backgroundView.clipsToBounds = true
                action.backgroundView.layer.cornerRadius = cornerRadius
                action.visualEffectView?.clipsToBounds = true
                action.visualEffectView?.layer.cornerRadius = cornerRadius
                action.button.clipsToBounds = true
                action.button.layer.cornerRadius = cornerRadius
                alertView.addSubview(action.button)
                alertViewHeight += buttonHeight
            }
        }
        switch style {
        case .actionSheet:
            cornerViewHeightConstraint.constant = cornerViewHeight
        default:
            alertViewHeight = cornerViewHeight
        }
        alertViewHeightConstraint?.constant = alertViewHeight
        view.layoutIfNeeded()
    }
    @objc dynamic func buttonWasTouchUpInside(_ sender: UIButton) {
        sender.isSelected = true
        let action = actions[sender.tag]
        dismiss(animated: true) {
            action.handler?(action)
        }
    }
}

// MARK: - PCLRespondsViewDelegate
extension PCLBlurEffectAlertController: PCLRespondsViewDelegate {
    func respondsViewDidTouch(_ view: UIView) {
        guard isActionSheet else { return }
        guard let cancelAction = cancelAction else { return }
        dismiss(animated: true) {
            cancelAction.handler?(cancelAction)
        }
    }
}

// MARK: - PCLAlertKeyboardNotificationObserver, PCLAlertActionEnabledDidChangeNotificationObserver
extension PCLBlurEffectAlertController : PCLAlertKeyboardNotificationObserver, PCLAlertActionEnabledDidChangeNotificationObserver {
    func keyboardWillHide(_ notification: Notification) {
        keyboardHeight = 0
        containerViewBottomLayoutConstraint.constant = keyboardHeight
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
    func keyboardWillShow(_ notification: Notification) {
        guard let userInfo = notification.userInfo as? [String: AnyObject],
            let keyboardSize = userInfo[UIResponder.keyboardFrameEndUserInfoKey]?.cgRectValue.size else {
                return
        }
        keyboardHeight = keyboardSize.height
        containerViewBottomLayoutConstraint.constant = -keyboardHeight
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
    func alertActionEnabledDidChange(_ notification: Notification) {
        actions.forEach { $0.button.isEnabled = $0.isEnabled }
    }
}

// MARK: - UIViewControllerTransitioningDelegate
extension PCLBlurEffectAlertController: UIViewControllerTransitioningDelegate {
    public func animationController(forPresented presented: UIViewController, presenting: UIViewController, source: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return PCLBlurEffectAlertTransitionAnimator(present: true)
    }
    public func animationController(forDismissed dismissed: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        return PCLBlurEffectAlertTransitionAnimator(present: false)
    }
}

// MARK: - UIApplication
private extension UIApplication {
    var topViewController: UIViewController? {
        guard var topViewController = UIApplication.shared.keyWindow?.rootViewController else { return nil }
        while let presentedViewController = topViewController.presentedViewController {
            topViewController = presentedViewController
        }
        return topViewController
    }
}
