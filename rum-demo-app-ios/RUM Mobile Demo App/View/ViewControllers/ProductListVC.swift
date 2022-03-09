//
//  ProductListVC.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import UIKit

class ProductListVC : UIViewController {
    
    @IBOutlet weak var collectionview: UICollectionView!
    
    @IBOutlet weak var collectionViewTopConstratint: NSLayoutConstraint!
    // MARK: - Injection
    let viewModel = ProductListVM()
    var products = [ProductList]() {
        didSet {
            DispatchQueue.main.async {
                self.collectionview.reloadData()
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.collectionview.register(UINib(nibName: "ProductListCell", bundle: nil), forCellWithReuseIdentifier: "ProductListCell")
        
        self.collectionview.register(UINib(nibName: "ProductListHeaderCell", bundle: nil), forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "ProductListHeaderCell")
        
        self.attemptFetchProductList()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.addBlueHeader(title: "RUM Mobile Demo", isRightButtonHidden: false, isBackButtonHidden: true)
        self.collectionview.layoutIfNeeded()
        
        RumEventHelper.shared.trackCustomRumEventFor(.productListLoaded)
    }
    
    // MARK: - Networking
    /**
     *description: Fetch product list API call and setting data.
     */
    private func attemptFetchProductList() {
        viewModel.fetchProducts { errorMessage, products in
            if let error = errorMessage {
                self.showAlertNativeSingleAction("Error", message: error)
                return
            }
            self.products = products
        }
        
        viewModel.showAlertClosure = {
            if let error = self.viewModel.error {
                let actionOK = PCLBlurEffectAlertAction(title: "OK".localized(), style: .default) {_ in }
                
                self.showAlertNativeSingleAction(StringConstants.alertTitle , message: error.localizedDescription)
                print(error.localizedDescription)
            }
        }
        
        viewModel.didFinishFetch = {
            // show data on view
            //update view logic goes here
        }
    }
}
extension ProductListVC :  UICollectionViewDataSource,UICollectionViewDelegateFlowLayout{
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
        // return 10
        return self.products.count
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        guard let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProductListCell", for: indexPath) as? ProductListCell else {
            return UICollectionViewCell()
        }
        let product = self.products[indexPath.row]
        cell.lblName.text = product.name.uppercased()
        cell.lblName.addTextSpacing(spacing: 4.5)
        cell.lblPrice.text = "\(product.priceUsd?.currencyCode ?? Constants.DefaultCurrencyCode) \(product.priceUsd?.price ?? 0)"
        
        cell.productImage.image = UIImage.init(named: product.picture)
        
        return cell
    }
    
}
extension ProductListVC: UICollectionViewDelegate {
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        handleNoInternetConnection {
            let vc = mainStoryBoard.instantiateViewController(withIdentifier: "ProductDetailVC") as! ProductDetailVC
            vc.product = self.products[indexPath.row]
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    func collectionView(_ collectionView: UICollectionView,
                        viewForSupplementaryElementOfKind kind: String,
                        at indexPath: IndexPath) -> UICollectionReusableView {
        
        switch kind {
            
        case UICollectionView.elementKindSectionHeader:
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "ProductListHeaderCell", for: indexPath) as! ProductListHeaderCell
            
            if UIDevice.current.userInterfaceIdiom == .pad{
                headerView.lblShipping.font = UIFont(name: config.fontNameMedium, size: config.largeLabelSize)
            }
            else{
                headerView.lblShipping.font = UIFont(name: config.fontNameMedium, size: config.midLargeLabelSize)
            }
            
            return headerView
            
        default:
            assert(false, "Unexpected element kind")
        }
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        var multiplier : CGFloat = 35.0
        if UIDevice.current.userInterfaceIdiom == .pad {
            multiplier = 50.0
        }
        let hh = self.view.frame.size.height * multiplier / 100  //294.0 == 34.84 %
        
        return CGSize(width: collectionView.frame.width, height:hh)
    }
}
