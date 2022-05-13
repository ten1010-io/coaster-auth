package io.ten1010.coaster.auth.domain;

public interface PasswordEncoder {

    String encode(CharSequence rawPassword);

}
