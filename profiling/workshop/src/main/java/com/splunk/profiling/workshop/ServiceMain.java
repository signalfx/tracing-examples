package com.splunk.profiling.workshop;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class ServiceMain {

    public static void main(String[] args) {
        port(9090);
        staticFiles.location("/public"); // Static files

        get("/hello", (req, res) -> "Hello World");
    }
}
