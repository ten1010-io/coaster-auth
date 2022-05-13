package io.ten1010.coaster.auth.web.jsonpatch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.github.fge.jackson.JacksonUtils;
import lombok.Getter;

import java.io.IOException;
import java.util.List;

@Getter
public class JsonPatch implements JsonSerializable {

    public static JsonPatch fromJson(final JsonNode node) throws IOException {
        return JacksonUtils.getReader().forType(JsonPatch.class).readValue(node);
    }

    private final List<JsonPatchOperation> operations;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public JsonPatch(final List<JsonPatchOperation> operations) {
        this.operations = List.copyOf(operations);
    }

    @Override
    public String toString() {
        return this.operations.toString();
    }

    @Override
    public void serialize(final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (final JsonPatchOperation op : this.operations) {
            op.serialize(jgen, provider);
        }
        jgen.writeEndArray();
    }

    @Override
    public void serializeWithType(final JsonGenerator jgen,
                                  final SerializerProvider provider,
                                  final TypeSerializer typeSer) throws IOException {
        serialize(jgen, provider);
    }

}
