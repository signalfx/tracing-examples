package com.signalfx.tracing.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentracing.Scope;
import io.opentracing.Tracer;


@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private Tracer tracer;

    /**
     * @return "heads" or "tails" to emulate a coin flip
     */
    @RequestMapping("/flip")
    public String flipACoin() throws Exception {
	// Emulate the coin flip
	String flipResult = trueWithProbability(.50) ? "heads" : "tails";

	// Tag the current Span with the result
	tracer.activeSpan().setTag("flipResult", flipResult);

	return flipResult;
    }

    /**
     * Returns false based on the passed in probability. 
     * @param probability - Expressed as a number between 0 and 1
     * @return
     */
    private boolean trueWithProbability(double probability) {
	// Create a new subspan called 'calculateOdds' that surrounds this logic 
	try (Scope scope = tracer.buildSpan("calculateOdds").startActive(true)) {
	    return Math.random() <= probability;
	} // By using the Java try-with-resources convention, the subspan is auto-closed
    }

    public static void main(String[] args) {
	SpringApplication.run(Application.class, args);
    }

}