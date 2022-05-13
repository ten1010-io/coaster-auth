package io.ten1010.coaster.auth.web.patcher;

import io.ten1010.coaster.auth.domain.DomainModel;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatchOperation;

public interface Patcher<T extends DomainModel> {

    boolean supports(JsonPatchOperation operation);

    void doPatch(T model, JsonPatchOperation operation);

}
