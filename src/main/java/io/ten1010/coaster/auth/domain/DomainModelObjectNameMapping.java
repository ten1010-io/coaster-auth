package io.ten1010.coaster.auth.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DomainModelObjectNameMapping {

    private static final Map<Class<? extends DomainModel>, String> byClass;
    private static final Map<String, Class<? extends DomainModel>> byName;

    static {
        byClass = new HashMap<>();
        byName = new HashMap<>();
        putNames();
        byClass.forEach((key, value) -> byName.put(value, key));
    }

    private static void putNames() {
        byClass.put(User.class, "user-domain-model");
    }

    public static Optional<String> findByClass(Class<? extends DomainModel> modelClass) {
        return Optional.ofNullable(byClass.get(modelClass));
    }

    public static Optional<Class<? extends DomainModel>> findByObjectName(String name) {
        return Optional.ofNullable(byName.get(name));
    }

}
