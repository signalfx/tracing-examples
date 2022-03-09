//
//  StaticEventsTests.swift
//  RUM Mobile Demo AppTests
//
//  Created by Akash Patel on 17/01/22.
//

import XCTest
@testable import RUM_Mobile_Demo_App

class StaticEventsTests: XCTestCase {
    
    var sut: StaticEventsVC!

    override func setUpWithError() throws {
        try? super.setUpWithError()
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        sut = storyboard.instantiateViewController(withIdentifier: "StaticEventsVC") as! StaticEventsVC
        sut.loadViewIfNeeded()
    }

    override func tearDownWithError() throws {
        sut = nil
        try? super.tearDownWithError()
    }

    func test5xxCall(){
        sut.btnFiveHundredError.sendActions(for: .touchUpInside)
    }
    
    // aschronous call test
    func test5xx_returnError(){
       let fivehundredexpection = self.expectation(description: "5xx")
       
       DataService.request( "https://mock.codes/500" , method: .get, params:[:], type: StaticEvents.self) { (staticevent, error , responsecode) in
            
            if let error = error {
                XCTFail(error.description)
                return
            }
            
            guard staticevent != nil else {
                XCTFail()
                return
            }
            
            fivehundredexpection.fulfill()
            XCTAssertGreaterThan(responsecode, 499)
            XCTAssertLessThan(responsecode , 600)
       }
        waitForExpectations(timeout: 8, handler: nil)
    }
    
    //performance testing
    func test5xx_Performance(){
        measure {
            sut.viewModel.staticEvent(withcode: 500)
        }
        
    }
    
    
   
}
