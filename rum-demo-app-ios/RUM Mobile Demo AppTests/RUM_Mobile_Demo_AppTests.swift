//
//  RUM_Mobile_Demo_AppTests.swift
//  RUM Mobile Demo AppTests
//
//  Created by Akash Patel on 17/01/22.
//

import XCTest
import Alamofire

@testable import RUM_Mobile_Demo_App


class RUM_Mobile_Demo_AppTests: XCTestCase {
    
    var loginvc = LoginVC()
    var staticeventvc = StaticEventsVC()

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

  
    // skip test conditionally  - login api test
    func testAPICall_CompleteWithStatusCode200() throws {
       
        try XCTSkipUnless(Connectivity.isConnectedToInternet, "Network connectivity needed for this test.")
         
        let e = expectation(description: "Alamofire api call")
        
        self.loginvc.viewModel.logIN()
            self.loginvc.viewModel.didFinishFetch = {
                e.fulfill()
                XCTAssertNil(self.loginvc.viewModel.error)  // error is nil
                XCTAssertEqual(self.loginvc.viewModel.responsecode , 200)
                
            
        }
        waitForExpectations(timeout: 5.0, handler: nil)
    }
    
    

}
