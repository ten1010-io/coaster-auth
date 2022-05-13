package io.ten1010.coaster.auth.web.exceptionadapter;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.domain.DomainModelObjectNameMapping;
import io.ten1010.coaster.auth.web.ApiResource;
import io.ten1010.coaster.auth.web.ApiResourceObjectNameMapping;
import io.ten1010.coaster.auth.web.DomainModelApiResourceMapping;

import java.util.List;
import java.util.Optional;

public class DomainModelPropertyExceptionAdapterManager {

    private List<DomainModelPropertyPathAdapter> domainModelPropertyPathAdapters;

    public DomainModelPropertyExceptionAdapterManager(List<DomainModelPropertyPathAdapter> domainModelPropertyPathAdapters) {
        this.domainModelPropertyPathAdapters = domainModelPropertyPathAdapters;
    }

    public boolean isTarget(PropertyUserVisibleException ex) {
        if (DomainModelObjectNameMapping.findByObjectName(ex.getObject()).isEmpty()) {
            return false;
        }
        Class<? extends DomainModel> modelClass = DomainModelObjectNameMapping.findByObjectName(ex.getObject()).get();
        if (DomainModelApiResourceMapping.findByDomainModel(modelClass).isEmpty()) {
            return false;
        }
        Class<? extends ApiResource> resourceClass = DomainModelApiResourceMapping.findByDomainModel(modelClass).get();
        if (ApiResourceObjectNameMapping.findByClass(resourceClass).isEmpty()) {
            return false;
        }

        return true;
    }

    public PropertyUserVisibleException adapt(PropertyUserVisibleException ex) {
        Class<? extends DomainModel> modelClass = DomainModelObjectNameMapping.findByObjectName(ex.getObject()).get();
        Class<? extends ApiResource> resourceClass = DomainModelApiResourceMapping.findByDomainModel(modelClass).get();
        String objectName = ApiResourceObjectNameMapping.findByClass(resourceClass).get();
        String propertyPath = ex.getPropertyPath();
        Optional<DomainModelPropertyPathAdapter> opt = findPropertyPathAdapter(modelClass);
        if (opt.isPresent()) {
            propertyPath = opt.get().adaptToApiResourcePropertyPath(ex.getPropertyPath());
        }

        return new PropertyUserVisibleException(objectName, propertyPath, ex.getMessage());
    }

    private Optional<DomainModelPropertyPathAdapter> findPropertyPathAdapter(Class<? extends DomainModel> modelClass) {
        for (DomainModelPropertyPathAdapter e : this.domainModelPropertyPathAdapters) {
            if (e.supports(modelClass)) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }

}
