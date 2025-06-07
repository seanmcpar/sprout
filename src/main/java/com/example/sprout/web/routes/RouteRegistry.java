package com.example.sprout.web.routes;

import com.example.sprout.annotations.ioc.sproutling.Controller;
import com.example.sprout.context.SproutlingContext;
import com.example.sprout.web.HttpMethod;

import java.util.*;

public class RouteRegistry {
    private final Map<String, RouteHandler> routes = new HashMap<>();

    public RouteRegistry(final SproutlingContext context) {
        context.getAllSproutlings().stream()
                .filter(sproutling -> sproutling.getClass().isAnnotationPresent(Controller.class))
                .forEach(this::registerSproutlingRoutes);
    }

    private void registerSproutlingRoutes(final Object sproutling) {
        for (final RouteDefinition routeDefinition : RouteScanner.scanRoutes(sproutling)) {
            registerRoute(sproutling, routeDefinition);
        }
    }

    private void registerRoute(final Object sproutling, final RouteDefinition routeDefinition) {
        final RouteHandler routeHandler = new RouteHandler(sproutling, routeDefinition.handler());
        final String key = routeKey(routeDefinition.method(), routeDefinition.path());
        addRouteIfValid(key, routeHandler);
    }

    private void addRouteIfValid(final String key, final RouteHandler routeHandler) {
        validateRoutesDoesNotContainKey(key);
        routes.put(key, routeHandler);
    }

    private void validateRoutesDoesNotContainKey(final String key) {
        if (routes.containsKey(key)) {
            throw new IllegalStateException("Duplicate route: " + key);
        }
    }

    public RouteHandler getHandler(final HttpMethod method, final String path) {
        return routes.get(routeKey(method, path));
    }

    public boolean pathExists(final String path) {
        return routes.keySet().stream()
                .anyMatch(k -> k.endsWith(":" + path));
    }

    private static String routeKey(final HttpMethod method, final String path) {
        return method.name() + ":" + path;
    }
}
