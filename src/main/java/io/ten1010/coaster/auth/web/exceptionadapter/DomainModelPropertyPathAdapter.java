package io.ten1010.coaster.auth.web.exceptionadapter;

import io.ten1010.coaster.auth.domain.DomainModel;

public interface DomainModelPropertyPathAdapter {

    boolean supports(Class<? extends DomainModel> modelClass);

    String adaptToApiResourcePropertyPath(String path);

}
