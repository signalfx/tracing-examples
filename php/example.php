<?php

// This example shows a basic PHP app that fetches blog posts from the
// symfony_demo app.  It shows traces being generated and propagated between
// two distinct apps.

namespace SignalFxTrace;

// This is required to get the tracer that is set automatically by the PHP
// extension.  This package is provided automatically by the extension and will
// be autoloaded.
use DDTrace\GlobalTracer;
use GuzzleHttp;
use Symfony\Component\DomCrawler\Crawler;

require_once __DIR__.'/vendor/autoload.php';

function run() {
    // "Manually" start a new span using the global tracer
    $scope = GlobalTracer::get()->startActiveSpan('root');
    $span = $scope->getSpan();

    // Add an arbitrary tag to the span
    $span->setTag("mytag", 'myvalue');

    try {
        // Do an auto-instrumented HTTP request
        $blogs_html = fetch_blogs();
        // Do a manually instrumented operation that is also traced
        $parsed_dom = parse_html($blogs_html);
    } catch (Exception $e) {
        // Inform the tracer that there was an exception thrown
        $span->setError($e);
        // Bubble up the exception
        throw $e;
    } finally {
        // Finish the span.  This should be done in a robust way that accounts
        // for exceptions and other non-happy path control flows.  A span
        // should generally always be finished if you want it to be reported.
        $span->finish();
    }

    print "DONE";
}

function fetch_blogs()
{
    // Guzzle is one of the auto-instrumented libraries, which means that it
    // will automatically generate spans without you having to do anything
    // special.  We are still in the scope of the span created in the `run()`
    // function, so all spans created by the Guzzle auto-instrumentation will
    // be child spans of that span.
    $client = new GuzzleHttp\Client(['timeout' => 15.0]);
    $res = $client->request('GET', 'localhost:8080/en/blog');

    return (string)$res->getBody();
}

function parse_html($html) {
    // Manually instrument the parsing of the HTML
    $scope = GlobalTracer::get()->startActiveSpan('parse_html');
    $span = $scope->getSpan();
    try{
        $crawler = new Crawler($html);
        // Set a tag on the span that gives more details about the operation.
        $span->setTag("nodeCount", $crawler->count());
        return $crawler;
    } finally {
        // Always finish the span
        $span->finish();
    }
}

run();
