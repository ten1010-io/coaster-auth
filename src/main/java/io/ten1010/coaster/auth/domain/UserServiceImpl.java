package io.ten1010.coaster.auth.domain;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import io.ten1010.coaster.auth.dao.UserRepository;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.regex.Pattern;

@Transactional
public class UserServiceImpl implements UserService {

    public static final String ADMIN_USER_ID = "adminuser";

    // at least 8 long, 1 lowercase letter, 1 uppercase letter, 1 number, 1 symbol
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&])[A-Za-z\\d$@!%*#?&]{8,100}$");

    private static void validatePassword(String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new PropertyUserVisibleException(User.OBJECT_NAME, "/password", "Invalid password");
        }
    }

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(UserDto dto) {
        if (dto.getUserId() != null) {
            if (!this.userRepository.findAllByUserId(dto.getUserId(), Pageable.unpaged()).isEmpty()) {
                throw new PropertyUserVisibleException(User.OBJECT_NAME, "/userId", UserServiceConstants.MSG_USER_ID_ALREADY_EXIST);
            }
        }
        if (dto.getPassword() == null) {
            throw new PropertyUserVisibleException(User.OBJECT_NAME, "/password", PropertyUserVisibleException.MSG_NULL_NOT_ALLOWED);
        }
        validatePassword(dto.getPassword());
        User user = User.builder()
                .setUserId(dto.getUserId())
                .setPassword(this.passwordEncoder.encode(dto.getPassword()))
                .setKoreanName(dto.getKoreanName())
                .setPhoneNumber(dto.getPhoneNumber())
                .setEmail(dto.getEmail())
                .setDepartment(dto.getDepartment())
                .build();

        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        if (user.getUserId().equals(ADMIN_USER_ID)) {
            return;
        }
        this.userRepository.delete(user);
    }

    @Override
    public void deleteUsers(Iterable<User> users) {
        users.forEach(this::deleteUser);
    }

}
