package io.ten1010.coaster.auth.web.patcher;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;

public class JsonPatchPropertyExceptionBuilder {

    public static final String OBJECT = "json-patch";

    public static JsonPatchPropertyExceptionBuilder op() {
        JsonPatchPropertyExceptionBuilder builder = new JsonPatchPropertyExceptionBuilder();
        builder.propertyPath = "/op";

        return builder;
    }

    public static JsonPatchPropertyExceptionBuilder path() {
        JsonPatchPropertyExceptionBuilder builder = new JsonPatchPropertyExceptionBuilder();
        builder.propertyPath = "/path";

        return builder;
    }

    public static JsonPatchPropertyExceptionBuilder value() {
        JsonPatchPropertyExceptionBuilder builder = new JsonPatchPropertyExceptionBuilder();
        builder.propertyPath = "/value";

        return builder;
    }

    public static JsonPatchPropertyExceptionBuilder from() {
        JsonPatchPropertyExceptionBuilder builder = new JsonPatchPropertyExceptionBuilder();
        builder.propertyPath = "/from";

        return builder;
    }

    public static JsonPatchPropertyExceptionBuilder testFailed() {
        return value().setMessage("Test failed");
    }

    private String object;
    private String propertyPath;
    private String message;

    private JsonPatchPropertyExceptionBuilder() {
        this.object = OBJECT;
    }

    public JsonPatchPropertyExceptionBuilder setMessage(String message) {
        this.message = message;

        return this;
    }

    public PropertyUserVisibleException build() {
        if (this.object == null || this.propertyPath == null || this.message == null) {
            throw new NullPointerException();
        }

        return new PropertyUserVisibleException(this.object, this.propertyPath, this.message);
    }

}
