package com.example.sprout.web;

import com.example.sprout.web.routes.RouteHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class ResponseWriter {

    public static void invokeHandlerAndRespond(final HttpExchange exchange, final RouteHandler handler) {
        try {
            final Object result = handler.method().invoke(handler.target());
            final byte[] bytes = (result == null ? "" : result.toString()).getBytes();

            exchange.sendResponseHeaders(200, bytes.length);
            try (final OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (final IllegalAccessException | InvocationTargetException e) {
            logError(e);
            sendError(exchange, 500, "Internal Server Error");
        } catch (final IOException e) {
            logError(e);
            safeClose(exchange);
        }
    }

    public static void sendError(final HttpExchange exchange, final int statusCode, final String message) {
        try {
            final byte[] bytes = message.getBytes();
            exchange.sendResponseHeaders(statusCode, bytes.length);
            try (final OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (final IOException e) {
            logError(e);
        } finally {
            safeClose(exchange);
        }
    }

    public static void handleNotFoundOrMethodNotAllowed(final HttpExchange exchange, final boolean pathExists) {
        try {
            exchange.sendResponseHeaders(pathExists ? 405 : 404, -1);
        } catch (final IOException e) {
            logError(e);
        } finally {
            safeClose(exchange);
        }
    }

    private static void safeClose(final HttpExchange exchange) {
        try {
            exchange.close();
        } catch (Exception ignore) { }
    }

    private static void logError(final Exception e) {
        e.printStackTrace();
    }
}
