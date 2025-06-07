package com.example.sprout.web.routes;

import com.example.sprout.annotations.rest.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteScanner {

    public static List<RouteDefinition> scanRoutes(final Object bean) {
        final List<RouteDefinition> routes = new ArrayList<>();
        final Class<?> clazz = bean.getClass();

        for (final Method method : clazz.getDeclaredMethods()) {
            for (final Annotation annotation : method.getAnnotations()) {
                final Optional<RouteDefinition> route = extractRouteDefinition(bean, method, annotation);
                route.ifPresent(routes::add);
            }
        }

        return routes;
    }

    private static Optional<RouteDefinition> extractRouteDefinition(final Object bean, final Method method,
                                                                    final Annotation annotation) {

        final RequestMapping requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
        if (requestMapping == null) return Optional.empty();

        try {
            final String path = getPath(annotation);
            return Optional.of(new RouteDefinition(requestMapping.method(), path, bean, method));
        } catch (final Exception exception) {
            throw new RuntimeException("Failed to extract route from method: " + method.getName(), exception);
        }
    }

    private static String getPath(final Annotation annotation) {
        try {
            final Method valueMethod = annotation.annotationType().getMethod("value");
            final String path = (String) valueMethod.invoke(annotation);
            return validatePath(annotation, path);
        } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            throw new RuntimeException(
                    "Failed to invoke 'value()' on @" + annotation.annotationType().getSimpleName(), exception);
        }
    }

    private static String validatePath(final Annotation annotation, final String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Empty path on @" + annotation.annotationType().getSimpleName());
        }
        return path;
    }

}

