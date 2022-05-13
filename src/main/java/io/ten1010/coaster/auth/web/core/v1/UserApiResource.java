package io.ten1010.coaster.auth.web.core.v1;

import io.ten1010.coaster.auth.web.ApiResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserApiResource extends ApiResource {

    @Nullable
    private String userId;
    @Nullable
    private String password;
    @Nullable
    private String koreanName;
    @Nullable
    private String phoneNumber;
    @Nullable
    private String email;
    @Nullable
    private String department;

}
