package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;

public class WebPropertyExceptionBuilder {

    public static final String OBJECT_HEADER = "http-header";
    public static final String OBJECT_PARAMETER = "http-parameter";
    public static final String OBJECT_BODY = "http-body";
    public static final String MSG_SINGLE_VALUE_ALLOWED_ONLY = "Single value allowed only";

    public static WebPropertyExceptionBuilder header() {
        WebPropertyExceptionBuilder builder = new WebPropertyExceptionBuilder();
        builder.object = OBJECT_HEADER;

        return builder;
    }

    public static WebPropertyExceptionBuilder parameter() {
        WebPropertyExceptionBuilder builder = new WebPropertyExceptionBuilder();
        builder.object = OBJECT_PARAMETER;

        return builder;
    }

    public static WebPropertyExceptionBuilder body() {
        WebPropertyExceptionBuilder builder = new WebPropertyExceptionBuilder();
        builder.object = OBJECT_BODY;

        return builder;
    }

    private String object;
    private String propertyPath;
    private String message;

    private WebPropertyExceptionBuilder() {
    }

    public WebPropertyExceptionBuilder setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;

        return this;
    }

    public WebPropertyExceptionBuilder setMessage(String message) {
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
