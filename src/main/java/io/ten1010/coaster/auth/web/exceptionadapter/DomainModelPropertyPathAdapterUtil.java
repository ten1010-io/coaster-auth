package io.ten1010.coaster.auth.web.exceptionadapter;

public class DomainModelPropertyPathAdapterUtil {

    public static boolean isMetadataPropertyPath(String path) {
        switch (path) {
            case "/id":
            case "/version":
            case "/creationTimestamp":
                return true;
            default:
                return false;
        }
    }

    public static String adaptToApiResourceMetadataPropertyPath(String path) {
        return "/metadata" + path;
    }

}
