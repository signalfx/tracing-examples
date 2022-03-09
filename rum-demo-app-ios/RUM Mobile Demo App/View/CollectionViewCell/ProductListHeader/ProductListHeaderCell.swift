//
//  ProductListHeaderCell.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 10/01/22.
//

import UIKit
import Foundation

class ProductListHeaderCell : UICollectionReusableView{
    
    @IBOutlet weak var lblShipping: UILabel!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
       

     }

     required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)

     }
    
}
