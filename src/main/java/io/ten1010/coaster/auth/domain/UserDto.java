package io.ten1010.coaster.auth.domain;

import lombok.*;

import javax.annotation.Nullable;

@Data
@Builder
@EqualsAndHashCode
@ToString
public class UserDto {

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
