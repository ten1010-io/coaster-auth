package io.ten1010.coaster.auth.web.meta.v1;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.annotation.Nullable;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class Status {

    private String type;
    @Nullable
    private Object detail;

}
