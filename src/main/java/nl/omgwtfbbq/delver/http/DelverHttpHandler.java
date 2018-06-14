package nl.omgwtfbbq.delver.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.omgwtfbbq.delver.PerformanceCollector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Very simple HTTP handler to view the method calls.
 */
public class DelverHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange h) throws IOException {
        StringWriter w = new StringWriter();
        PerformanceCollector.instance().write(w);
        String response = w.toString();
        h.sendResponseHeaders(200, response.length());
        h.getResponseHeaders().add("Content-Type", "text/plain");
        OutputStream os = h.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
