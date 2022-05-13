package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "op")
@JsonSubTypes({
        @Type(name = "add", value = AddOperation.class),
        @Type(name = "copy", value = CopyOperation.class),
        @Type(name = "move", value = MoveOperation.class),
        @Type(name = "remove", value = RemoveOperation.class),
        @Type(name = "replace", value = ReplaceOperation.class),
        @Type(name = "test", value = TestOperation.class)
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public abstract class JsonPatchOperation implements JsonSerializable {

    protected final PatchOperationType op;
    protected final JsonPointer path;

    protected JsonPatchOperation(final String op, final JsonPointer path) {
        this.op = PatchOperationType.getByName(op).orElseThrow();
        this.path = path;
    }

    @Override
    public abstract String toString();

}
