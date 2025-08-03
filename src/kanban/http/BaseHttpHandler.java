package kanban.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    protected void ok(HttpExchange h, String json) throws IOException {
        sendResponse(h, 200, json);
    }

    protected void created(HttpExchange h) throws IOException {
        sendResponse(h, 201, "");
    }

    protected void notFound(HttpExchange h) throws IOException {
        sendResponse(h, 404, "{\"error\":\"Not Found\"}");
    }

    protected void notAcceptable(HttpExchange h) throws IOException {
        sendResponse(h, 406, "{\"error\":\"Not Acceptable\"}");
    }

    protected void error(HttpExchange h) throws IOException {
        sendResponse(h, 500, "{\"error\":\"Internal Server Error\"}");
    }

    protected void methodNotAllowed(HttpExchange h) throws IOException {
        sendResponse(h, 405, "{\"error\":\"Method Not Allowed\"}");
    }
}
