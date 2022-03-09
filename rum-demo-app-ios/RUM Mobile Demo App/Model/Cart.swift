//
//  Cart.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit


//MARK: - app has one cart, more items are there in one cart
final class Cart {
    static let sharedInstance = Cart()
    var cartItems : [pickedProduct]?
    
    private init(){
        cartItems = [pickedProduct]()
    }
    
    /**
     *description: Based on the current items added in the cart, display a count on Tabbar item as a badge count.
    */
    func setItemsCountAsBadge(){
        let itemCount = self.getItemsCountToShowAsBadge()
        
        let tabContoller = window?.rootViewController as? SlideAnimatedTabbarController
        tabContoller?.tabBar.addBadge(atIndex: 1, badge: itemCount)
       
    }
    
    /**
     *description: Returns the count of items added in the cart.
    */
    func getItemsCountToShowAsBadge() -> Int{
        var itemCount = 0
        let selectedProducts  = self.cartItems!
        for product in selectedProducts {
            itemCount += product.quantity
        }
        
        return itemCount
    }
    
   
}



//MARK: - selected item

class pickedProduct {
    var product : ProductList
    var quantity: Int = 1
   
    init(product:ProductList ,quantity:Int){
        self.product = product
        self.quantity = quantity
    }
}
