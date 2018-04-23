package com.signalfx.tracing.examples;

import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;


public class App
{
    public static void main(String[] args)
    {
        String accessToken = args[0];
        Tracing.initTracing(accessToken);

        while (true) {
            try (Scope parent = GlobalTracer.get()
                    .buildSpan("hello")
                    .startActive(true)) {
                long sum = 0;
                for (int i = 0; i < 1000000000; i++) {
                    sum += i;
                }
                System.out.println("Hello");
                try (Scope child = GlobalTracer.get()
                        .buildSpan("world")
                        .startActive(true)) {
                    for (int i = 0; i < 100000000; i++) {
                        sum += i;
                    }
                    System.out.println("World");
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                return;
            }
        }

    }
}
