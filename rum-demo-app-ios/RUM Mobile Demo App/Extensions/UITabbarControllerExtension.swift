//
//  UITabbarControllerExtension.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 10/01/22.
//
import UIKit
import Foundation

// MARK: - UITabbarcontroller sub class
class SlideAnimatedTabbarController: UITabBarController {

    override func viewDidLoad() {
      super.viewDidLoad()
      self.delegate = self
      self.tabBar.tintColor = config.buttonBackGroundColor
      self.tabBar.unselectedItemTintColor = UIColor.black

    }
   
}

extension SlideAnimatedTabbarController : UITabBarControllerDelegate {
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
            animateSliding(fromController: selectedViewController, toController: viewController)
            return false
        }


func animateSliding(fromController: UIViewController?, toController: UIViewController?) {
        
        guard let fromController = fromController, let toController = toController  else { return }
        guard let fromIndex = self.viewControllers?.firstIndex(of: fromController),
        let toIndex = self.viewControllers?.firstIndex(of: toController) else { return }
        guard fromIndex != toIndex else { return }
        
        let fromView = fromController.view!
        let toView = toController.view!
        let viewSize = fromView.frame
        let scrollRight = fromIndex < toIndex
        fromView.superview?.addSubview(toView)
        toView.frame = CGRect(x: scrollRight ? screenWidth : -screenWidth,
                              y: viewSize.origin.y,
                              width: screenWidth,
                              height: viewSize.height)

        func animate() {
            fromView.frame = CGRect(x: scrollRight ? -screenWidth : screenWidth,
                                    y: viewSize.origin.y,
                                    width: screenWidth,
                                    height: viewSize.height)
            toView.frame = CGRect(x: 0,
                                    y: viewSize.origin.y,
                                    width: screenWidth,
                                    height: viewSize.height)
        }
        
        func finished(_ completed: Bool) {
            fromView.removeFromSuperview()
            self.selectedIndex = toIndex
        }
        
        UIView.animate(withDuration: 0.35, delay: 0, options: .curveEaseInOut,
                       animations: animate, completion: finished)
    }
    
    func animateToTab(toIndex: Int,completionHandler: @escaping (_ success:Bool) -> Void) {
        DispatchQueue.main.async {
            guard let tabViewControllers = self.viewControllers,
                  let selectedVC = self.selectedViewController else { return }
            
            guard let fromView = selectedVC.view,
                  let toView = tabViewControllers[toIndex].view,
                  let fromIndex = tabViewControllers.firstIndex(of: selectedVC),
                  fromIndex != toIndex else { return }
            
            
            // Add the toView to the tab bar view
            fromView.superview?.addSubview(toView)
            
            // Position toView off screen (to the left/right of fromView)
            let screenWidth = UIScreen.main.bounds.size.width
            let scrollRight = toIndex > fromIndex
            let offset = (scrollRight ? screenWidth : -screenWidth)
            toView.center = CGPoint(x: fromView.center.x + offset, y: toView.center.y)
            
            // Disable interaction during animation
            self.view.isUserInteractionEnabled = false
            
            UIView.animate(withDuration: 0.3,
                           delay: 0.0,
                           usingSpringWithDamping: 1,
                           initialSpringVelocity: 0,
                           options: .curveEaseOut,
                           animations: {
                // Slide the views by -offset
                fromView.center = CGPoint(x: fromView.center.x - offset, y: fromView.center.y)
                toView.center = CGPoint(x: toView.center.x - offset, y: toView.center.y)
                
            }, completion: { finished in
                // Remove the old view from the tabbar view.
                fromView.removeFromSuperview()
                self.selectedIndex = toIndex
                self.view.isUserInteractionEnabled = true
                completionHandler(true)
            })
        }
    }
    
}


// MARK: - Tabbar for showing badge
extension UITabBar {
    func addBadge(atIndex:Int,badge:Int) {
        if badge > 0{
            if let tabItems = self.items {
                let tabItem = tabItems[atIndex]
                tabItem.badgeValue = "\(badge)"     //"●"
                tabItem.badgeColor = .red
                tabItem.setBadgeTextAttributes([NSAttributedString.Key.foregroundColor: UIColor.white], for: .normal)
            }
        }
        else{
            self.removeBadge(fromIndex: atIndex)
        }
        
    }
    func removeBadge(fromIndex:Int) {
        DispatchQueue.main.async {
            if let tabItems = self.items {
                let tabItem = tabItems[fromIndex]
                tabItem.badgeValue = nil
            }
        }
    }
    
 }
// MARK: - textfield with left image
@IBDesignable
class DesignableUITextField: UITextField {
    
    // Provides left padding for images
    override func leftViewRect(forBounds bounds: CGRect) -> CGRect {
        var textRect = super.leftViewRect(forBounds: bounds)
        textRect.origin.x += leftPadding
        return textRect
    }
    
    @IBInspectable var leftImage: UIImage? {
        didSet {
            updateView()
        }
    }
    
    @IBInspectable var leftPadding: CGFloat = 0
    
    @IBInspectable var color: UIColor = UIColor.lightGray {
        didSet {
            updateView()
        }
    }
    
    func updateView() {
        if let image = leftImage {
            leftViewMode = UITextField.ViewMode.always
            let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 20, height: 20))
            imageView.contentMode = .scaleAspectFit
            imageView.image = image
            // Note: In order for your image to use the tint color, you have to select the image in the Assets.xcassets and change the "Render As" property to "Template Image".
                   imageView.tintColor = color
                   leftView = imageView
               } else {
                   leftViewMode = UITextField.ViewMode.never
                   leftView = nil
               }
               
               // Placeholder text color
               attributedPlaceholder = NSAttributedString(string: placeholder != nil ?  placeholder! : "", attributes:[NSAttributedString.Key.foregroundColor: color])
           }
}
