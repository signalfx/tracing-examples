import UIKit
import SDWebImage

class ProductListViewController: UITableViewController {
    static let showDetailSequeIdentifier = "ShowProductDetailSegue"
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == Self.showDetailSequeIdentifier,
           let destination = segue.destination as? ProductDetailViewController,
           let cell = sender as? UITableViewCell,
           let indexPath = tableView.indexPath(for: cell) {
            let product = Product.testData[indexPath.row]
            destination.configure(with: product)
        }
    }
}

extension ProductListViewController {
    static let productListCellIdentifier = "ProductListCell"
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Product.testData.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: Self.productListCellIdentifier, for: indexPath) as? ProductListCell else {
            fatalError("Unable to dequeue ProductCell")
        }
        
        let formatter : NumberFormatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "$"
        formatter.minimumFractionDigits = 2
        
        let product = Product.testData[indexPath.row]
        cell.nameLabel.text = product.name
        cell.priceLabel.text = formatter.string(from: NSNumber(value: product.price))!
        cell.productImage.sd_setImage(with: product.image)
        return cell
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let cartButton = UIBarButtonItem(title: "Cart", style: .plain, target: self, action: #selector(self.crashIt))
        self.navigationItem.rightBarButtonItem = cartButton
    }
    
    @objc func crashIt() {
        print("crash coming...")
        let null = UnsafePointer<UInt8>(bitPattern: 0)
        let _derefNull = null!.pointee
    }
}

