//
//  PCLBlurEffectAlert.swift
//  Pods
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit

open class PCLBlurEffectAlert {
    public enum ActionStyle {
        case `default`, cancel, destructive
        var isCancel: Bool {
            return self == .cancel
        }
    }
    public enum ControllerStyle {
        case actionSheet, alert, alertVertical
    }
}

// MARK: - RespondsView
extension PCLBlurEffectAlert {
    final class RespondsView: UIView {
        weak var delegate: PCLRespondsViewDelegate?
        required init?(coder aDecoder: NSCoder) {
            super.init(coder: aDecoder)
        }
        override init(frame: CGRect) {
            super.init(frame: frame)
        }
        override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
            super.touchesEnded(touches, with: event)
            delegate?.respondsViewDidTouch(self)
        }
    }
}

// MARK: - PCLRespondsViewDelegate
protocol PCLRespondsViewDelegate: AnyObject {
    func respondsViewDidTouch(_ view: UIView)
}

// MARK: - NotificationManager
extension PCLBlurEffectAlert {
    struct NotificationManager {
        static let shared = PCLBlurEffectAlert.NotificationManager()
        fileprivate let notificationCenter = NotificationCenter.default
    }
}

// MARK: - keyboardWillShow/Hide
@objc protocol PCLAlertKeyboardNotificationObserver: AnyObject {
    func keyboardWillShow(_ notification: Notification)
    func keyboardWillHide(_ notification: Notification)
}

extension PCLBlurEffectAlert.NotificationManager {
    func addKeyboardNotificationObserver(_ observer: PCLAlertKeyboardNotificationObserver) {
        notificationCenter.addObserver(observer,
                                       selector: #selector(PCLAlertKeyboardNotificationObserver.keyboardWillShow(_:)),
                                       name: UIResponder.keyboardWillShowNotification,
                                       object: nil)
        notificationCenter.addObserver(observer,
                                       selector: #selector(PCLAlertKeyboardNotificationObserver.keyboardWillHide(_:)),
                                       name: UIResponder.keyboardWillHideNotification,
                                       object: nil)
    }
    func removeKeyboardNotificationObserver(_ observer: PCLAlertKeyboardNotificationObserver) {
        notificationCenter.removeObserver(observer,
                                          name: UIResponder.keyboardWillShowNotification,
                                          object: nil)
        notificationCenter.removeObserver(observer,
                                          name: UIResponder.keyboardWillHideNotification,
                                          object: nil)
    }
    func postKeyboardWillShowNotification() {
        notificationCenter.post(Notification(name: UIResponder.keyboardWillShowNotification,
                                             object: nil))
    }
    func postKeyboardWillHideNotification() {
        notificationCenter.post(Notification(name: UIResponder.keyboardWillHideNotification,
                                             object: nil))
    }
}


// MARK: - AlertActionEnabledDidChange
@objc protocol PCLAlertActionEnabledDidChangeNotificationObserver: AnyObject {
    func alertActionEnabledDidChange(_ notification: Notification)
}
// MARK: - private
extension PCLBlurEffectAlert.NotificationManager {
    private typealias Manager = PCLBlurEffectAlert.NotificationManager
    private static let AlertActionEnabledDidChangeNotification = "AlertActionEnabledDidChangeNotification"
    func addAlertActionEnabledDidChangeNotificationObserver(_ observer: PCLAlertActionEnabledDidChangeNotificationObserver) {
        notificationCenter.addObserver(observer,
                                       selector: #selector(PCLAlertActionEnabledDidChangeNotificationObserver.alertActionEnabledDidChange(_:)),
                                       name: Notification.Name(rawValue: Manager.AlertActionEnabledDidChangeNotification),
                                       object: nil)
    }
    func removeAlertActionEnabledDidChangeNotificationObserver(_ observer: PCLAlertActionEnabledDidChangeNotificationObserver) {
        notificationCenter.removeObserver(observer,
                                          name: Notification.Name(rawValue: Manager.AlertActionEnabledDidChangeNotification),
                                          object: nil)
    }
    func postAlertActionEnabledDidChangeNotification() {
        notificationCenter.post(Notification(name: Notification.Name(rawValue: Manager.AlertActionEnabledDidChangeNotification),
                                             object: nil,
                                             userInfo: nil))
    }
}
