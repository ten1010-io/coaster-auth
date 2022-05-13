package io.ten1010.coaster.auth.web.core.v1;

import com.fasterxml.jackson.databind.JsonNode;
import io.ten1010.coaster.auth.domain.User;
import io.ten1010.coaster.auth.web.jsonpatch.AddOperation;
import io.ten1010.coaster.auth.web.jsonpatch.JsonPatchOperation;
import io.ten1010.coaster.auth.web.jsonpatch.PatchOperationType;
import io.ten1010.coaster.auth.web.jsonpatch.ReplaceOperation;
import io.ten1010.coaster.auth.web.patcher.JsonPatchPropertyExceptionBuilder;
import io.ten1010.coaster.auth.web.patcher.Patcher;

public class EmailPatchers {

    private static void replace(User model, JsonNode value) {
        if (!value.isTextual()) {
            throw JsonPatchPropertyExceptionBuilder
                    .value()
                    .setMessage("Value ust be string")
                    .build();
        }
        model.patchEmail(value.textValue());
    }

    public static Patcher<User> addPatcher() {
        return new Patcher<>() {

            @Override
            public boolean supports(JsonPatchOperation operation) {
                if (!operation.getOp().equals(PatchOperationType.ADD)) {
                    return false;
                }
                return operation.getPath().toString().equals("/email");
            }

            @Override
            public void doPatch(User model, JsonPatchOperation operation) {
                AddOperation addOperation = (AddOperation) operation;
                replace(model, addOperation.getValue());
            }

        };
    }

    public static Patcher<User> replacePatcher() {
        return new Patcher<>() {

            @Override
            public boolean supports(JsonPatchOperation operation) {
                if (!operation.getOp().equals(PatchOperationType.REPLACE)) {
                    return false;
                }
                return operation.getPath().toString().equals("/email");
            }

            @Override
            public void doPatch(User model, JsonPatchOperation operation) {
                ReplaceOperation replaceOperation = (ReplaceOperation) operation;
                replace(model, replaceOperation.getValue());
            }

        };
    }

}
