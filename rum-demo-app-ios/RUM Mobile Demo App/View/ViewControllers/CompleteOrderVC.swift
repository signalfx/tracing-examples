//
//  CompleteOrderVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit

class CompleteOrderVC  : UIViewController{
    
    @IBOutlet weak var btnKeepBrowsing: RoundedCornerButton!
    
    @IBOutlet weak var heightOfTableView: NSLayoutConstraint!
    @IBOutlet weak var lblCompleteOrder: UILabel!
    @IBOutlet weak var tblOrderInfo: UITableView!
    @IBOutlet weak var viewOrderSummaryContainer: UIView!
    
    var orderInfoArray = [["key":"Order Confirmation ID",
                           "value": "\(randomString(length: 4))-\(randomString(length: 8))-\(randomString(length: 8))-\(randomString(length: 8))-\(randomString(length: 4))"],
                          
                          ["key":"Shipping Tracking ID",
                           "value":"\(randomString(length: 2).uppercased())-\(randomString(length: 4, onlyNumbers: true))-\(randomString(length: 8, onlyNumbers: true))"],
                          
                          ["key":"Shipping Cost",
                           "value":"USD 67.43"],
                          
                          ["key":"Total Paid",
                           "value":"USD 111.00"]]
    override func viewDidLoad() {
        super.viewDidLoad()
        
        RumEventHelper.shared.trackCustomRumEventFor(.paymentSuccessful)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBlueHeader(title: "", isRightButtonHidden: true, isBackButtonHidden: true)
        btnKeepBrowsing.addTextSpacing()
        
        DispatchQueue.main.async {
            self.viewOrderSummaryContainer.addShadow()
            self.heightOfTableView.constant = self.tblOrderInfo.contentSize.height
            self.view.layoutIfNeeded()
        }
    }
    
    @IBAction func btnKeepBrowsingClicked(_ sender: Any) {
        // pop to product list
        //show card tab selected.
        DispatchQueue.main.async {
            CartVM().emptyCart() {
                DispatchQueue.main.async {
                    sharedCart.cartItems?.removeAll()
                    self.navigationController?.popToRootViewController(animated: false)
                    
                    let tabContoller = window?.rootViewController as? SlideAnimatedTabbarController
                    //tabContoller?.selectedIndex = 0
                    tabContoller?.animateToTab(toIndex: 0, completionHandler: {success in })
                    tabContoller?.tabBar.removeBadge(fromIndex: 1)
                }
            }
        }
        
    }
}

extension CompleteOrderVC : UITableViewDelegate,UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return orderInfoArray.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell : UITableViewCell = tableView.dequeueReusableCell(withIdentifier: "OrderCell", for: indexPath)
        let lblInfo = cell.viewWithTag(100) as! UILabel
        let lblValue = cell.viewWithTag(101)  as! UILabel
        let dict = orderInfoArray[indexPath.row]
        
        lblInfo.text = "\(String(describing: dict["key"]!))"
        lblValue.text = "\(String(describing: dict["value"]!))"
        
        
        return cell
    }
}
