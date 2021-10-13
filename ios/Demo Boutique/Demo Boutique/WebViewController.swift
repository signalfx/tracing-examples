import UIKit
import WebKit
import SplunkOtel

class WebViewController: UIViewController {
    
    @IBOutlet var webView: WKWebView!

    override func viewDidLoad() {
        super.viewDidLoad()
        SplunkRum.integrateWithBrowserRum(self.webView)

        self.webView.load(URLRequest(url: URL(string: "http://ssidhu.o11ystore.com/")!))
    }
}
