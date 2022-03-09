//
//  NavMenu.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 15/02/22.
//

import UIKit

class NavMenu: UIView {
    
    var actionHandler : (()->Void)?

    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    class func showNavMenu(clickHandler : (()->Void)? = nil) {
        guard let menuView = Bundle.main.loadNibNamed("NavMenu", owner: self, options: nil)?[0] as? NavMenu else {return}
        let keyWindow = UIApplication.shared.keyWindow
        menuView.frame = keyWindow?.bounds ?? .zero
        menuView.actionHandler = clickHandler
        keyWindow?.addSubview(menuView)
    }
    
    @IBAction func btnMenuAction(_ sender : UIButton) {
        actionHandler?()
    }
    
    @IBAction func btnForDismissViewPressed(_ sender : UIButton) {
        self.removeFromSuperview()
    }
}
