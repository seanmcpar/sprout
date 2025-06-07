package com.example.sprout.boot;

import com.example.sprout.annotations.SproutApplication;
import com.example.sprout.context.SproutlingContext;
import com.example.sprout.web.SproutWebServer;

import java.io.IOException;

public class SproutApplicationRunner {

    public static void run(final Class<?> mainClass) {
        if (!mainClass.isAnnotationPresent(SproutApplication.class)) {
            throw new IllegalStateException("Missing @SproutApplication on main class");
        }

        final SproutlingContext context = createSproutlingContext(mainClass);
        try {
            SproutWebServer server = new SproutWebServer(8080, context);
            server.start();
        } catch (final IOException exception) {
            throw new RuntimeException("IOException occurred when starting Sprout Web Server", exception);
        }
    }

    private static SproutlingContext createSproutlingContext(final Class<?> mainClass) {
        final String basePackage = mainClass.getPackage().getName();
        return new SproutlingContext(basePackage);
    }

}

