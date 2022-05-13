package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.web.core.v1.UserApiResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ApiResourceObjectNameMapping {

    private static final Map<Class<? extends ApiResource>, String> byClass;
    private static final Map<String, Class<? extends ApiResource>> byName;

    static {
        byClass = new HashMap<>();
        byName = new HashMap<>();
        putObjectNames();
        byClass.forEach((key, value) -> byName.put(value, key));
    }

    private static void putObjectNames() {
        byClass.put(UserApiResource.class, "user");
    }

    public static Optional<String> findByClass(Class<? extends ApiResource> modelClass) {
        return Optional.ofNullable(byClass.get(modelClass));
    }

    public static Optional<Class<? extends ApiResource>> findByObjectName(String name) {
        return Optional.ofNullable(byName.get(name));
    }

}
