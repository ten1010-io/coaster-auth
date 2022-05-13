package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import lombok.Getter;

import java.io.IOException;

@Getter
public abstract class DualPathOperation extends JsonPatchOperation {

    @JsonSerialize(using = ToStringSerializer.class)
    protected final JsonPointer from;

    protected DualPathOperation(final String op, final JsonPointer from, final JsonPointer path) {
        super(op, path);
        this.from = from;
    }

    @Override
    public final void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("op", this.op.getName());
        jgen.writeStringField("path", this.path.toString());
        jgen.writeStringField("from", this.from.toString());
        jgen.writeEndObject();
    }

    @Override
    public final void serializeWithType(final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer)
            throws IOException, JsonProcessingException {
        serialize(jgen, provider);
    }

    @Override
    public final String toString() {
        return "op: " + this.op + "; from: \"" + this.from + "\"; path: \"" + this.path + '"';
    }

}
