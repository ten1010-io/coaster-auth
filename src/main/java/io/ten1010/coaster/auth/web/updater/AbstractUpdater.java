package io.ten1010.coaster.auth.web.updater;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.web.ApiResource;
import io.ten1010.coaster.auth.web.ApiResourceObjectNameMapping;
import io.ten1010.coaster.auth.web.exception.ResourceConflictException;
import io.ten1010.coaster.auth.web.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public abstract class AbstractUpdater<Model extends DomainModel, Resource extends ApiResource> implements Updater<Model, Resource> {

    private JpaRepository<Model, Long> repository;

    protected AbstractUpdater(JpaRepository<Model, Long> repository) {
        this.repository = repository;
    }

    @Transactional
    public Model update(long id, Resource resource) {
        Optional<Model> opt = this.repository.findById(id);
        if (opt.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        if (resource.getMetadata() == null) {
            throw new PropertyUserVisibleException(
                    ApiResourceObjectNameMapping.findByClass(resource.getClass()).orElseThrow(),
                    "/metadata",
                    PropertyUserVisibleException.MSG_NULL_NOT_ALLOWED);
        }
        if (!opt.get().getVersion().get().equals(resource.getMetadata().getVersion())) {
            throw new ResourceConflictException();
        }
        updateInternal(opt.get(), resource);

        return opt.get();
    }

    protected abstract void updateInternal(Model model, Resource resource);

}
