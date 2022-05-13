package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.web.meta.v1.Metadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class ApiResource {

    @Nullable
    protected Metadata metadata;

}
