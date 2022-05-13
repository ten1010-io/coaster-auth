package io.ten1010.coaster.auth.common;

public class UserVisibleException extends RuntimeException {

    public UserVisibleException(String message) {
        super(message);
    }

    public UserVisibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserVisibleException(Throwable cause) {
        super(cause);
    }

}
