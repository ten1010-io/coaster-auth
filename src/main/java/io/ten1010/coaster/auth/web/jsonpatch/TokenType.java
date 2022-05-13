package io.ten1010.coaster.auth.web.jsonpatch;

public enum TokenType {

    FIELD, INDEX_NUMBER, INDEX_LAST;

    public static TokenType resolve(String token) {
        if (token.equals("-")) {
            return INDEX_LAST;
        }
        try {
            Integer.parseInt(token);
            return INDEX_NUMBER;
        } catch (NumberFormatException e) {
            return FIELD;
        }
    }

}
