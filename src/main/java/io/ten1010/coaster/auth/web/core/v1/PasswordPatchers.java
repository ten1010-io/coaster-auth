package io.ten1010.coaster.auth.web.core.v1;

import com.fasterxml.jackson.databind.JsonNode;
import io.ten1010.coaster.auth.domain.PasswordEncoder;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.jsonpatch.AddOperation;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatchOperation;
import io.ten1010.coaster.auth.web.jsonpatch.PatchOperationType;
import io.ten1010.coaster.auth.web.jsonpatch.ReplaceOperation;
import io.ten1010.coaster.auth.web.patcher.JsonPatchPropertyExceptionBuilder;
import io.ten1010.coaster.auth.web.patcher.Patcher;

public class PasswordPatchers {

    private static void replace(User model, JsonNode value, PasswordEncoder encoder) {
        if (!value.isTextual()) {
            throw JsonPatchPropertyExceptionBuilder
                    .value()
                    .setMessage("Value ust be string")
                    .build();
        }
        String password = value.textValue();
        model.patchPassword(encoder.encode(password));
    }

    public static Patcher<User> addPatcher(PasswordEncoder encoder) {
        return new Patcher<>() {

            @Override
            public boolean supports(JsonPatchOperation operation) {
                if (!operation.getOp().equals(PatchOperationType.ADD)) {
                    return false;
                }
                return operation.getPath().toString().equals("/password");
            }

            @Override
            public void doPatch(User model, JsonPatchOperation operation) {
                AddOperation addOperation = (AddOperation) operation;
                replace(model, addOperation.getValue(), encoder);
            }

        };
    }

    public static Patcher<User> replacePatcher(PasswordEncoder encoder) {
        return new Patcher<>() {

            @Override
            public boolean supports(JsonPatchOperation operation) {
                if (!operation.getOp().equals(PatchOperationType.REPLACE)) {
                    return false;
                }
                return operation.getPath().toString().equals("/password");
            }

            @Override
            public void doPatch(User model, JsonPatchOperation operation) {
                ReplaceOperation replaceOperation = (ReplaceOperation) operation;
                replace(model, replaceOperation.getValue(), encoder);
            }

        };
    }

}
