package com.example.sprout.web.routes;

import com.example.sprout.web.HttpMethod;

import java.lang.reflect.Method;

public record RouteDefinition(HttpMethod method, String path, Object bean, Method handler) {}
