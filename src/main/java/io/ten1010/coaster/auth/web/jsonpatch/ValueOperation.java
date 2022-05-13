package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import lombok.Getter;

import java.io.IOException;

@Getter
public abstract class ValueOperation extends JsonPatchOperation {

    @JsonSerialize
    protected final JsonNode value;

    protected ValueOperation(final String op, final JsonPointer path, final JsonNode value) {
        super(op, path);
        this.value = value.deepCopy();
    }

    @Override
    public final void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("op", this.op.getName());
        jgen.writeStringField("path", this.path.toString());
        jgen.writeFieldName("value");
        jgen.writeTree(this.value);
        jgen.writeEndObject();
    }

    @Override
    public final void serializeWithType(final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer)
            throws IOException, JsonProcessingException {
        serialize(jgen, provider);
    }

    @Override
    public final String toString() {
        return "op: " + op + "; path: \"" + this.path + "\"; value: " + this.value;
    }

}
