import UIKit
import OpenTelemetryApi

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
             + " (plus $" + calculateShipping() + " shipping)"
        self.productImage.sd_setImage(with: product.image)
        self.descriptionTextView.text = product.description

        self.navigationItem.title = product.name
    }

     // Here is an example of manual instrumentation
     func calculateShipping() -> String {
         let tracer = OpenTelemetry.instance.tracerProvider.get(instrumentationName: "manual_example", instrumentationVersion: "1.0")
         let span = tracer.spanBuilder(spanName: "calculateShipping").startSpan()
         span.setAttribute(key: "shipping.currency", value: "USD")
         defer {
             // using defer ensures that the span will end even if the method throws
             // if you want to capture exception details in the span, you'll need a try/catch block
             span.end()
         }

         sleep(2) // this is a very difficult and expensive calculation!
         return "5.95"
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
