package io.ten1010.coaster.auth.web.jsonpatch;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public enum PatchOperationType {

    ADD("add"), COPY("copy"), MOVE("move"), REMOVE("remove"), REPLACE("replace"),
    TEST("test");

    private static final Map<String, PatchOperationType> BY_NAME;

    static {
        BY_NAME = new HashMap<>();
        PatchOperationType[] arr = PatchOperationType.values();
        for (int i = 0; i < PatchOperationType.values().length; i++) {
            BY_NAME.put(arr[i].getName(), arr[i]);
        }
    }

    public static Optional<PatchOperationType> getByName(String name) {
        return Optional.ofNullable(BY_NAME.get(name.toLowerCase()));
    }

    private String name;

    PatchOperationType(String name) {
        this.name = name;
    }

}
