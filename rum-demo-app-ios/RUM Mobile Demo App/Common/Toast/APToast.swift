//
//  APToast.swift
//  Project_chat
//
//  Created by Akash Patel on 25/02/22.
//

import Foundation
import UIKit

public class APToast : UIView {
    
    @IBOutlet weak var lblMessage : UILabel!
    @IBOutlet weak var viewContainer : UIView!
    
    public override func awakeFromNib() {
        super.awakeFromNib()
        
        self.viewContainer.layer.cornerRadius = 8
        self.viewContainer.layer.masksToBounds = true
    }
    
    /// Display a toast message on the screen with provided paramters to configure the UI of toast.
    ///
    /// This adds the toast message which will be dismissed from the parent view after defined seconds.
    /// - Parameters:
    ///   - message: The message needs to be display in a toast.
    ///   - backgroundColor: The background colour of the toast view appearing. Default is set to black.
    ///   - bgColorAlpha: If need a transparancy for the toast view, add the alpha for the background colour
    ///   - textColor: Color of the text displaying message in the toast view
    ///   - screenTime: After how manu seconds, the toast view needs to be dismiss itself from the screen.
    ///   - dismissHandler: Call back to handle any additional events when the toast view is dismissed from the view.
    public static func showToastWith(message: String, backgroundColor : UIColor = .black, bgColorAlpha: CGFloat = 0.7, textColor: UIColor = .white, screenTime: TimeInterval = 3.0, dismissHandler: (()->Void)? = nil) {
        
        DispatchQueue.main.async {
            
            guard let toastView = Bundle.init(for: self.classForCoder()).loadNibNamed("APToast", owner: nil, options: nil)?[0] as? APToast else {
                return
            }
            
            toastView.manageFrameAndDataWith(message: message, backgroundColor: backgroundColor, bgColorAlpha: bgColorAlpha, textColor: textColor)
            toastView.alpha = 0
            (UIApplication.shared.delegate as? AppDelegate)?.window?.addSubview(toastView)
            
            UIView.animate(withDuration: 0.3) {
                toastView.alpha = 1
            }
            
            DispatchQueue.main.asyncAfter(deadline: .now() + screenTime) {
                toastView.dismissToastView(compleion: dismissHandler)
            }
        }
    }
    
    /// Display a toast message on the screen with provided paramters to configure the UI of toast.
    ///
    /// This adds the toast message stays on the screen until dismissed manually using completion handler.
    /// - Parameters:
    ///   - message: The message needs to be display in a toast.
    ///   - backgroundColor: The background colour of the toast view appearing. Default is set to black.
    ///   - bgColorAlpha: If need a transparancy for the toast view, add the alpha for the background colour
    ///   - textColor: Color of the text displaying message in the toast view
    ///   - screenTime: After how manu seconds, the toast view needs to be dismiss itself from the screen.
    ///   - handler: Completion handler which provides the object of displayed toast view and can be used to dismiss the view from the screen after any defined external events.
    public static func showRigidToastWith(message: String, backgroundColor : UIColor = .black, bgColorAlpha: CGFloat = 0.7, textColor: UIColor = .white, handler: @escaping (_ toastView : APToast)->Void) {
        
        DispatchQueue.main.async {
            guard let toastView = Bundle.init(for: self.classForCoder()).loadNibNamed("APToast", owner: nil, options: nil)?[0] as? APToast else {
                return
            }
            
            toastView.manageFrameAndDataWith(message: message, backgroundColor: backgroundColor, bgColorAlpha: bgColorAlpha, textColor: textColor)
            toastView.alpha = 0
            (UIApplication.shared.delegate as? AppDelegate)?.window?.addSubview(toastView)
            (UIApplication.shared.delegate as? AppDelegate)?.window?.bringSubviewToFront(toastView)
            
            UIView.animate(withDuration: 0.3) {
                toastView.alpha = 1
            } completion: { finished in
                handler(toastView)
            }
        }
    }
    
    
    /// Private function which handles the frame layout and UI of the Toast View
    /// - Parameters:
    ///   - message: Set the text on the label outlet
    ///   - backgroundColor: Add the background color to the
    ///   - bgColorAlpha: Alpha color for the background of the toast view
    ///   - textColor: Sets the label text colour on the toast view
    fileprivate func manageFrameAndDataWith(message: String, backgroundColor : UIColor = .black, bgColorAlpha: CGFloat = 0.7, textColor: UIColor = .white){
        
        self.lblMessage.text = message
        self.lblMessage.numberOfLines = 0
        self.lblMessage.textColor = textColor
        self.viewContainer.backgroundColor = backgroundColor.withAlphaComponent(bgColorAlpha)
        self.lblMessage.sizeToFit()
        
        
        let viewHeight = self.lblMessage.frame.height + 36
        let viewFrameY = UIScreen.main.bounds.height - (50 + ((UIApplication.shared.delegate as? AppDelegate)?.window?.safeAreaInsets.top ?? 0 != 0 ? 30 : 0) + viewHeight * 0.7)
        self.frame = CGRect.init(x: 0, y: viewFrameY, width: UIScreen.main.bounds.width, height: viewHeight)
    }
    
    
    /// Dismiss the toast view with animation from the parent view
    /// - Parameter compleion: Call back to handle any external action after the toast view is dismissed.
    func dismissToastView(compleion : (()->Void)? = nil) {
        UIView.animate(withDuration: 0.5) {
            self.alpha = 0
        } completion: { finished in
            compleion?()
            self.removeFromSuperview()
        }
    }
}
