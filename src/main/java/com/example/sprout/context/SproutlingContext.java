package com.example.sprout.context;

import com.example.sprout.annotations.ioc.InjectDependencies;
import com.example.sprout.annotations.ioc.sproutling.Sproutling;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class SproutlingContext {

    private final Map<Class<?>, Object> registry = new HashMap<>();

    public SproutlingContext(String basePackage) {
        Reflections reflections = new Reflections(basePackage, Scanners.TypesAnnotated);

        Set<Class<? extends Annotation>> sproutlingAnnotations = reflections.getTypesAnnotatedWith(Sproutling.class).stream()
                .filter(Class::isAnnotation)
                .map(a -> (Class<? extends Annotation>) a)
                .collect(Collectors.toSet());

        Set<Class<?>> sproutClasses = new HashSet<>();
        for (Class<? extends Annotation> annotation : sproutlingAnnotations) {
            sproutClasses.addAll(reflections.getTypesAnnotatedWith(annotation));
        }

        for (Class<?> clazz : sproutClasses) {
            createInstance(clazz);
        }
    }

    private Object createInstance(Class<?> clazz) {
        if (registry.containsKey(clazz)) {
            return registry.get(clazz);
        }

        try {
            Constructor<?> injectConstructor = null;
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                if (constructor.isAnnotationPresent(InjectDependencies.class)) {
                    injectConstructor = constructor;
                    break;
                }
            }

            Object instance;
            if (injectConstructor != null) {
                Class<?>[] paramTypes = injectConstructor.getParameterTypes();
                Object[] paramInstances = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramInstances[i] = createInstance(paramTypes[i]);
                }
                injectConstructor.setAccessible(true);
                instance = injectConstructor.newInstance(paramInstances);
            } else {
                Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
                defaultConstructor.setAccessible(true);
                instance = defaultConstructor.newInstance();
            }

            registry.put(clazz, instance);
            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate: " + clazz.getName(), e);
        }
    }

    public <T> T getSproutling(Class<T> type) {
        return type.cast(registry.get(type));
    }

    public Collection<Object> getAllSproutlings() {
        return registry.values();
    }
}
