package io.ten1010.coaster.auth.web.updater;

import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.web.ApiResource;

import javax.transaction.Transactional;

public interface Updater<Model extends DomainModel, Resource extends ApiResource> {

    @Transactional
    Model update(long id, Resource resource);

}
