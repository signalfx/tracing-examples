package sf.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.signalfx.tracing.api.Trace;
import com.signalfx.tracing.context.TraceScope;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

// The span name is set below as "MyOperation"
// The custom tag for the span is set below with key:value "MyTag", "CustomTag"		

public class EchoDemo extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
        HttpServletResponse response) throws IOException, ServletException {
	try (Scope scope = GlobalTracer.get().buildSpan("MyOperation").startActive(true)) {
   	    scope.span().setTag("MyTag", "CustomTag");
       	    response.setContentType("text/plain;charset=utf-8");
       	    response.setStatus(HttpServletResponse.SC_OK);
       	    baseRequest.setHandled(true);
       	    response.getWriter().println("Hello world");
	    scope.close();
        }
    }
    public static void main(String[] args) throws Exception {
        Server server = new Server(5000);
        server.setHandler(new EchoDemo());
        server.start();
        server.join();
    }
}
