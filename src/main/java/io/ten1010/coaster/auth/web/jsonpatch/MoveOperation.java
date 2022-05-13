package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.fge.jackson.jsonpointer.JsonPointer;

public final class MoveOperation extends DualPathOperation {

    @JsonCreator
    public MoveOperation(@JsonProperty("from") final JsonPointer from, @JsonProperty("path") final JsonPointer path) {
        super(PatchOperationType.MOVE.getName(), from, path);
    }

}
