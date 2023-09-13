package com.splunk.tracing.otel.examples.cloudfoundry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class CfDemoApplication {
  public static void main(String[] args) {
    SpringApplication.run(CfDemoApplication.class, args);
  }
}
