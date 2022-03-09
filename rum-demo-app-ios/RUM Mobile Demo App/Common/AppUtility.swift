//
//  AppUtility.swift
//  RUM Mobile Demo App
//
//  Created by Akash Patel on 06/01/22.
//

import Foundation
import SplunkOtel
import OpenTelemetryApi
import OpenTelemetrySdk

func handleException(errorstring : String){
    SplunkRum.reportError(string: errorstring)
}
func handleException(error : Error){
    SplunkRum.reportError(error: error)
}
func handleException(exception : NSException){
    SplunkRum.reportError(exception: exception)
}

class Connectivity {
    class var isConnectedToInternet:Bool {
        return Reachability.isConnectedToNetwork()
    }
}

func manualSpan(spanName:String,dict:[String: String]) {
  let tracer = OpenTelemetrySDK.instance.tracerProvider.get(instrumentationName: config.RUM_TRACER_NAME )
  let span = tracer.spanBuilder(spanName: spanName).startSpan()
    let keys = dict.keys
    for key in keys{
        span.setAttribute(key: key, value: dict[key]!)
    }
  span.end() // or use defer for this
}
