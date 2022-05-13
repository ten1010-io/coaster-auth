package io.ten1010.coaster.auth.domain;

import javax.transaction.Transactional;

@Transactional
public interface UserService {

    User createUser(UserDto dto);

    void deleteUser(User user);

    void deleteUsers(Iterable<User> users);

}
