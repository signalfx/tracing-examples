package com.splunk.profiling.workshop;

import static spark.Spark.get;
import static spark.Spark.port;

public class ServiceMain {

    public static void main(String[] args) {
        System.out.println("Hello");
        port(9090);
        get("/hello", (req, res) -> "Hello World");
    }
}
