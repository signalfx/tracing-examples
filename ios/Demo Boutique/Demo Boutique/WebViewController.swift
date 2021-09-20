import UIKit
import WebKit

class WebViewController: UIViewController {
    
    @IBOutlet var webView: WKWebView!

    override func viewDidLoad() {
        super.viewDidLoad()

        self.webView.load(URLRequest(url: URL(string: "http://ssidhu.o11ystore.com/")!))
    }
}
