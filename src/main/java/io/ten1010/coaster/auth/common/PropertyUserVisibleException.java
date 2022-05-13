package io.ten1010.coaster.auth.common;

import lombok.Getter;

@Getter
public class PropertyUserVisibleException extends UserVisibleException {

    public static final String MSG_NULL_NOT_ALLOWED = "Null not allowed";

    private String object;
    private String propertyPath;

    public PropertyUserVisibleException(String object, String propertyPath, String message) {
        super(message);
        this.object = object;
        this.propertyPath = propertyPath;
    }

}
