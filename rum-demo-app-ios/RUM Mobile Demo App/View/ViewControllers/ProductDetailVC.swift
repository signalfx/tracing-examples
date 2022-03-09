//
//  ProductDetailVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import UIKit
import Foundation

class ProductDetailVC: UIViewController{
    
    @IBOutlet weak var collectionview: UICollectionView!
            
    let viewModel = ProductListVM()
    let detailViewModel = ProductDetailVM()
    let cartViewModel = CartVM()
    
    var product : ProductList?
    var likeProducts = [ProductList]() {
        didSet {
            DispatchQueue.main.async {
                self.collectionview.reloadData()
            }
        }
    }
    var productQuantity : Int = 1
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.collectionview.register(UINib(nibName: "ProductListCell", bundle: nil), forCellWithReuseIdentifier: "ProductListCell")
        self.collectionview.contentInset = UIEdgeInsets.init(top: 0, left: 0, bottom: 20, right: 0)
        
        if let productName = self.product?.name, !productName.isEmpty {
            RumEventHelper.shared.trackCustomRumEventFor(.productViewed, additionalAppendingString: productName, attributes: ["product.name" : productName])
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: false)
        
        self.detailViewModel.fetchProductDetails(productID: self.product?.id ?? "0") {
            self.viewModel.fetchProducts { errorMessage, products in
                self.likeProducts = products
                
                self.handleEventStories()
            }
        }
    }
    
    //MARK: - Event Stories
    
    func handleEventStories() {
        //Check if there is error type is defined for the selected product
        if !(self.product?.errorType.isEmpty ?? true) {
            
            if self.product?.errorType.lowercased() == ProductErrorType.freeze.rawValue {
                //Freeze the app
                StaticEventsVM().freezeApp()
            } else if self.product?.errorType.lowercased() == ProductErrorType.error4xx.rawValue {
                //Generate 4xx error request
                StaticEventsVM().staticEvent(withcode: 400)
            } else if self.product?.errorType.lowercased() == ProductErrorType.error5xx.rawValue {
                //Generate 4xx error request
                StaticEventsVM().staticEvent(withcode: 500)
            } else if self.product?.errorType.lowercased() == ProductErrorType.error4xx.rawValue {
                //Generate 4xx error request
                StaticEventsVM().staticEvent(withcode: 400)
            }
        }
    }
    
    //MARK: -
    /**
     *description: Action when user click on the "Add to Cart" button on product details screen
    */
    @objc func btnAddToCartClicked() {
        
        handleNoInternetConnection {
            //Check if there is error action "cart" is defined for the selected product
            if self.product?.errorAction.lowercased() == ProductErrorAction.cart.rawValue {
                
                if self.product?.errorType.lowercased() == ProductErrorType.crash.rawValue {
                    //Crash the app
                    StaticEventsVM().crashApp()
                }
            }
            
            //Add custom Rum event
            RumEventHelper.shared.trackCustomRumEventFor(.addToCart)
            
            //add current product(items with quantity) in to cart shared instance
            let selectedProduct = pickedProduct(product: self.product!, quantity: self.productQuantity)
            self.cartViewModel.addItemToCart(item: selectedProduct)
            
            self.navigationController?.popToRootViewController(animated: false)
            
            //show card tab selected.
            let tabContoller = window?.rootViewController as? SlideAnimatedTabbarController
            tabContoller?.animateToTab(toIndex: 1, completionHandler: {success in })
        }
    }
    
}


extension ProductDetailVC : UICollectionViewDelegateFlowLayout , UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {

        var noOfCellsInRow = 2
        if UIDevice.current.userInterfaceIdiom == .pad{
            noOfCellsInRow = 3
        }

        let flowLayout = collectionViewLayout as! UICollectionViewFlowLayout

        let totalSpace = flowLayout.sectionInset.left
            + flowLayout.sectionInset.right
            + (flowLayout.minimumInteritemSpacing * CGFloat(noOfCellsInRow - 1))

        let size = Int((collectionView.bounds.width - totalSpace) / CGFloat(noOfCellsInRow))

        return CGSize(width: size, height: 230)
    }
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return self.likeProducts.count
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
   
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProductListCell", for: indexPath) as? ProductListCell else {
            return UICollectionViewCell()
        }
        let product = self.likeProducts[indexPath.row]
        cell.lblName.text = product.name.uppercased()
        cell.lblName.addTextSpacing(spacing: 4.5)
        cell.lblPrice.text = "\(product.priceUsd?.currencyCode ?? Constants.DefaultCurrencyCode) \(product.priceUsd?.price?.description ?? "00.00")"
        
        cell.productImage.image = UIImage.init(named: product.picture)
       
        return cell
    }
    
}

extension ProductDetailVC: UICollectionViewDelegate {

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        handleNoInternetConnection {
            guard let vc = mainStoryBoard.instantiateViewController(withIdentifier: "ProductDetailVC") as? ProductDetailVC else { return }
            vc.product = self.likeProducts[indexPath.item]
            self.navigationController?.pushViewController(vc, animated: true)
        }

    }
    
    func collectionView(_ collectionView: UICollectionView,
                        viewForSupplementaryElementOfKind kind: String,
                        at indexPath: IndexPath) -> UICollectionReusableView {

        switch kind {

        case UICollectionView.elementKindSectionHeader:
            
            guard
                let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "ProductDetailsHeader", for: indexPath) as? ProductDetailsHeader else {
                    fatalError("Invalid view type")
            }
            
            DispatchQueue.main.async {
                headerView.btnAddToCart.addTextSpacing()
                headerView.btnAddToCart.addTarget(self, action: #selector(self.btnAddToCartClicked), for: .touchUpInside)
               
                headerView.lblProductName.text = self.product?.name.uppercased()
                headerView.lblProductName.addTextSpacing(spacing: 4.5)
                headerView.lblPrice.text = "\(self.product?.priceUsd?.currencyCode ?? Constants.DefaultCurrencyCode) \(self.product?.priceUsd?.price ?? 0)"
                
                headerView.productImgView.image = UIImage.init(named: self.product?.picture ?? "")
                
                headerView.lblProductDescription.text = self.product?.description
                headerView.txtQty.text = "\(self.productQuantity)"
                headerView.qtyUpdatedCallBack = { (qty) -> Void in
                    
                    if self.product?.errorType.lowercased() == ProductErrorType.exception.rawValue {
                        if (Int(qty) ?? 1) > self.product?.availableQty ?? 1 {
                            let errorMessage = "Maximum available stock size is 1 and the quantity added by the user is > 1 , an exception will be generated and the user will not be able to add it to the cart."
                            headerView.txtQty.endEditing(true)
                            handleException(errorstring: errorMessage)
                            self.showAlertNativeSingleAction("Error", message: errorMessage)
                            headerView.txtQty.text = self.productQuantity.description
                        } else {
                            self.productQuantity = Int(qty) ?? 1
                        }
                    }
                }
            }
            
            return headerView

        default:
            assert(false, "Unexpected element kind")
        }
    }
    
    @objc func openPicker() {
        
    }
    
}
