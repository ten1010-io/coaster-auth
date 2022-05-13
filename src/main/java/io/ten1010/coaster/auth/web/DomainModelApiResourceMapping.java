package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.core.v1.UserApiResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DomainModelApiResourceMapping {

    private static final Map<Class<? extends DomainModel>, Class<? extends ApiResource>> byDomainModel;
    private static final Map<Class<? extends ApiResource>, Class<? extends DomainModel>> byApiResource;

    static {
        byDomainModel = new HashMap<>();
        byApiResource = new HashMap<>();
        putMappings();
        byDomainModel.forEach((key, value) -> byApiResource.put(value, key));
    }

    private static void putMappings() {
        byDomainModel.put(User.class, UserApiResource.class);
    }

    public static Optional<Class<? extends ApiResource>> findByDomainModel(Class<? extends DomainModel> modelClass) {
        return Optional.ofNullable(byDomainModel.get(modelClass));
    }

    public static Optional<Class<? extends DomainModel>> findByApiResource(Class<? extends ApiResource> resourceClass) {
        return Optional.ofNullable(byApiResource.get(resourceClass));
    }

}
