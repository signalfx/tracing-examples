/*
 * Copyright Splunk Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.splunk.support.lambda.examples;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class ApiGatewayRequestFunction
    implements RequestHandler<APIGatewayProxyRequestEvent, Object> {

  @Override
  public Object handleRequest(APIGatewayProxyRequestEvent input, Context context) {

    System.out.println("RequestApiGatewayWrapperFunction: " + input.getBody());
    return new APIGatewayProxyResponseEvent().withBody("Hi " + input.getBody()).withStatusCode(200);
  }
}
