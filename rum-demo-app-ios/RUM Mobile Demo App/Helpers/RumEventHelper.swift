//
//  RumEventHelper.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel3 on 25/01/22.
//

import Foundation
import OpenTelemetryApi
import OpenTelemetrySdk
import SplunkOtel
import SplunkOtelCrashReporting
import UIKit
import CoreLocation

class RumEventHelper {
    
    static let shared = RumEventHelper()
    
    fileprivate let SplunkRumVersionString = "0.5.1"
    fileprivate var tracer : Tracer {
        get {
            return OpenTelemetry.instance.tracerProvider.get(instrumentationName: "splunk-ios", instrumentationVersion: SplunkRumVersionString)
        }
    }
    
    var shouldFailPayment = false
    
    enum RumCustomEvent : String {
        case timeToReady = "Time_to_Ready"
        case slowAPI = "Slow API"
        case productListLoaded = "ProductListLoaded"
        case productViewed = "ProductDetails_"
        case cart = "Cart"
        case addToCart = "AddToCart"
        case checkout = "Checkout"
        case placeOrder = "PlaceOrder"
        case paymentSuccessful = "Payment_Success"
        case paymentFailed = "Payment_Failed"
    }
    
    /**
     Track custom events with addtional attributes to be add with the custom event.
    */
    func trackCustomRumEventFor(_ event : RumCustomEvent, additionalAppendingString : String? = nil, attributes : [String : String]? = nil) {
        
        var eventName = event.rawValue
        
        if let appendString = additionalAppendingString {
            eventName = eventName.appending(appendString)
        }
        
        let span = tracer.spanBuilder(spanName: eventName).startSpan()
        span.setAttribute(key: "workflow.name", value:
                            eventName)
        
        if let attr = attributes {
            for eachkey in Array(attr.keys) {
                span.setAttribute(key: eachkey, value: attr[eachkey] ?? "")
            }
        }
        
        span.end()
    }
    
    /**
     Track errors with custom name
     */
    func addError(_ errorName: String, attributes : [String : String]? = nil) {
        
        let span = tracer.spanBuilder(spanName: errorName).startSpan()
        span.setAttribute(key: "component", value: "error")
        span.setAttribute(key: "error", value: true)
        span.setAttribute(key: "workflow.name", value:
                            errorName)
        span.setAttribute(key: "exception.type", value: errorName)
        
        if let attr = attributes {
            for eachkey in Array(attr.keys) {
                span.setAttribute(key: eachkey, value: attr[eachkey] ?? "")
            }
        }
        
        span.end()
    }
    
    
    /// This starts the span with the span name provided. This method just starts the span. To end the span, use the object of span provided in completion.
    /// - Parameters:
    ///   - spanName: The span name which needs to be set
    ///   - shouldCreateWorkflow: If this sets to true, then workflow.name attribute will be set to the created span otherwise it won't add that attribute.
    ///   - parentSpan: provide parent span if any. This is an optional parameter.
    ///   - attributes: Any additional attributes needs to be added. This is an optional parameter.
    ///   - completion: Callback when span is created and started, it gives span object for further use as parameter of call back.
    func startSpanWith(spanName: String, shouldCreateWorkflow: Bool = false, parentSpan: Span?, attributes : [String : String]? = nil, completion : @escaping (_ spanObject : Span?)->Void) {
        
        var span : Span?
        
        if let parent = parentSpan {
            span = tracer.spanBuilder(spanName: spanName).setParent(parent).startSpan()
        } else {
            span = tracer.spanBuilder(spanName: spanName).startSpan()
        }
        
        if shouldCreateWorkflow {
            span?.setAttribute(key: "workflow.name", value:
                                spanName)
        }
        
        if let attr = attributes {
            for eachkey in Array(attr.keys) {
                span?.setAttribute(key: eachkey, value: attr[eachkey] ?? "")
            }
        }
        
        completion(span)
    }
    
    func handleLocationBasedAPICall() {
        //FRANCE: 46.2276, 2.2137
        //Uncomment below line and comment the line after that when you need a static location of France.
        //let location = CLLocation.init(latitude: 46.2276, longitude: 2.2137)
        let location = CLLocation.init(latitude: coordinate.latitude, longitude: coordinate.longitude)
        location.fetchCountry { country, error in
            if let countryName = country, !countryName.isEmpty {
//                print("======>>>> COUNTRY => \(countryName)")
                if countryName.lowercased() == "france" {
                    self.shouldFailPayment = true
                    DispatchQueue.main.async {
                        DataService.request(getURL(for: "\(ApiName.GenerateSalesTax.rawValue)?country=\(countryName.lowercased())"), method: "GET", params: nil, shouldDisplayLoader: false, type: ProductDetail.self) { responseModel, errorMessage, responseCode in}
                    }
                } else {
                    RumEventHelper.shared.shouldFailPayment = false
                }
            } else {
                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    self.handleLocationBasedAPICall()
                }
            }
        }
    }
}
