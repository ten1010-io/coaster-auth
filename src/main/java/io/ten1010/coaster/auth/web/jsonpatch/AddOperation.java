package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;

public final class AddOperation extends ValueOperation {

    @JsonCreator
    public AddOperation(@JsonProperty("path") final JsonPointer path, @JsonProperty("value") final JsonNode value) {
        super(PatchOperationType.ADD.getName(), path, value);
    }

}
