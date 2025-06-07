package com.example.sprout.web;

import com.example.sprout.context.SproutlingContext;
import com.example.sprout.web.routes.RouteHandler;
import com.example.sprout.web.routes.RouteRegistry;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

import static com.example.sprout.web.ResponseWriter.handleNotFoundOrMethodNotAllowed;
import static com.example.sprout.web.ResponseWriter.invokeHandlerAndRespond;

public class SproutWebServer {
    private final HttpServer server;
    private final RouteRegistry routeRegistry;

    public SproutWebServer(final int port, final SproutlingContext context) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        routeRegistry = new RouteRegistry(context);

        server.createContext("/", this::dispatch);
        server.setExecutor(null);

    }

    private void dispatch(final HttpExchange exchange) {
        final HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
        final String path = exchange.getRequestURI().getPath();

        final RouteHandler handler = routeRegistry.getHandler(method, path);
        if (handler == null) {
            boolean pathExists = routeRegistry.pathExists(path);
            handleNotFoundOrMethodNotAllowed(exchange, pathExists);
        } else {
            invokeHandlerAndRespond(exchange, handler);
        }
    }

    public void start() {
        System.out.println("🌱 Sprout Web Server listening on port " + server.getAddress().getPort());
        server.start();
    }

}
