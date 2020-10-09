package com.splunk.tracing.otel.examples.cloudfoundry;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class GetExampleController {
  private final OkHttpClient httpClient = new OkHttpClient();

  @ResponseBody
  @GetMapping("/get/example")
  public String getExample() throws IOException {
    long start = System.currentTimeMillis();

    Request rq = new Request.Builder().get().url("http://www.example.com").build();
    Response rs = httpClient.newCall(rq).execute();
    log.info("Response size: {}", rs.body() != null ? rs.body().string().length() : -1);

    return "Done in " + (System.currentTimeMillis() - start) + "ms";
  }
}
