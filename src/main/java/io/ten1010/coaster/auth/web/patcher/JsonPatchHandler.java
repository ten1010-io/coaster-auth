package io.ten1010.coaster.auth.web.patcher;

import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.web.exception.ResourceNotFoundException;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatch;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatchOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class JsonPatchHandler<T extends DomainModel> {

    private JpaRepository<T, Long> repository;
    private List<Patcher<T>> patchers;

    public JsonPatchHandler(JpaRepository<T, Long> repository, List<Patcher<T>> patchers) {
        this.repository = repository;
        this.patchers = patchers;
    }

    @Transactional
    public T handle(long id, JsonPatch patch) {
        Optional<T> modelOpt = this.repository.findById(id);
        if (modelOpt.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        for (JsonPatchOperation operation : patch.getOperations()) {
            boolean patched = false;
            for (Patcher<T> patcher : this.patchers) {
                if (patcher.supports(operation)) {
                    patcher.doPatch(modelOpt.get(), operation);
                    patched = true;
                    break;
                }
            }
            if (!patched) {
                throw JsonPatchPropertyExceptionBuilder
                        .path()
                        .setMessage("Not supported")
                        .build();
            }
        }

        return modelOpt.get();
    }

}
