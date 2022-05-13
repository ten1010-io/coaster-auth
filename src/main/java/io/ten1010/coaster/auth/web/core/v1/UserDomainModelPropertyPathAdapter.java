package io.ten1010.coaster.auth.web.core.v1;

import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.exceptionadapter.DomainModelPropertyPathAdapter;
import io.ten1010.coaster.auth.web.exceptionadapter.DomainModelPropertyPathAdapterUtil;

public class UserDomainModelPropertyPathAdapter implements DomainModelPropertyPathAdapter {

    public boolean supports(Class<? extends DomainModel> modelClass) {
        return modelClass.equals(User.class);
    }

    public String adaptToApiResourcePropertyPath(String path) {
        if (DomainModelPropertyPathAdapterUtil.isMetadataPropertyPath(path)) {
            return DomainModelPropertyPathAdapterUtil.adaptToApiResourceMetadataPropertyPath(path);
        }

        return path;
    }

}
