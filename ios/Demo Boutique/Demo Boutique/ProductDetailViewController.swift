import UIKit

class ProductDetailViewController: UIViewController {
    @IBOutlet var productImage: UIImageView!
    @IBOutlet var nameLabel: UILabel!
    @IBOutlet var priceLabel: UILabel!
    @IBOutlet var descriptionTextView: UITextView!
    
    var product: Product?
    
    func configure(with product: Product) {
        self.product = product
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let formatter : NumberFormatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "$"
        formatter.minimumFractionDigits = 2

        let product = self.product!
        self.nameLabel.text = product.name
        self.priceLabel.text = formatter.string(from: NSNumber(value: product.price))!
        self.productImage.sd_setImage(with: product.image)
        self.descriptionTextView.text = product.description

        self.navigationItem.title = product.name
    }
    
    @IBAction func addToCartButtonTriggered(_ sender: UIButton) {
        let url = URL(string: "http://httpstat.us/404?sleep=1000")!
        let req = URLRequest(url: url)
        let task = URLSession.shared.dataTask(with: req) {(data, response: URLResponse?, _) in
            guard
                let response = response as? HTTPURLResponse
            else { return }

            print("Add to cart response")
            print(response.statusCode)
        }

        task.resume()
    }
}
