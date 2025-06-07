package com.example.sprout.web.routes;

import java.lang.reflect.Method;

public record RouteHandler(Object target, Method method) {}