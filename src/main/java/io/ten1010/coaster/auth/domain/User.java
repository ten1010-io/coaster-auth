package io.ten1010.coaster.auth.domain;

import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Optional;
import java.util.regex.Pattern;

import static io.ten1010.coaster.auth.domain.User.COL_NAME_USER_ID;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {COL_NAME_USER_ID})})
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(onlyExplicitlyIncluded = true)
public class User extends DomainModel {

    public static final String COL_NAME_USER_ID = "user_Id";
    public static final String OBJECT_NAME = DomainModelObjectNameMapping.findByClass(User.class).orElseThrow();

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern NOT_LETTER_OR_NUMBER_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    private static final Pattern KOREAN_NAME_PATTERN = Pattern.compile("^[가-힣]{2,4}$");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[0-9a-zA-Z가-힣]([ 0-9a-zA-Z가-힣]*[0-9a-zA-Z가-힣])?$");

    public static Builder builder() {
        return new Builder();
    }

    private static void validateUserId(String userId) {
        if (userId.length() < 6 || userId.length() > 30) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/userId", "Length must be between 6 and 30");
        }
        if (UPPERCASE_PATTERN.matcher(userId).find()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/userId", "Can not contain uppercase letter");
        }
        if (NOT_LETTER_OR_NUMBER_PATTERN.matcher(userId).find()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/userId", "Only letter or number allowed");
        }
    }

    private static void validateKoreanName(String koreanName) {
        if (!KOREAN_NAME_PATTERN.matcher(koreanName).matches()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/koreanName", "Invalid korean name");
        }
    }

    private static void validatePhoneNumber(String phoneNumber) {
        if (!PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/phoneNumber", "Invalid phone number");
        }
    }

    private static void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/email", "Invalid email address");
        }
    }

    private static void validateDepartment(String department) {
        if (!DEPARTMENT_PATTERN.matcher(department).matches()) {
            throw new PropertyUserVisibleException(OBJECT_NAME, "/department", "Invalid department");
        }
    }

    public static class Builder {

        private User user;

        public Builder() {
            this.user = new User();
        }

        public Builder setUserId(@Nullable String userId) {
            this.user.userId = userId;

            return this;
        }

        public Builder setPassword(@Nullable String password) {
            this.user.password = password;

            return this;
        }

        public Builder setKoreanName(@Nullable String koreanName) {
            this.user.koreanName = koreanName;

            return this;
        }

        public Builder setPhoneNumber(@Nullable String phoneNumber) {
            this.user.phoneNumber = phoneNumber;

            return this;
        }

        public Builder setEmail(@Nullable String email) {
            this.user.email = email;

            return this;
        }

        public Builder setDepartment(@Nullable String department) {
            this.user.department = department;

            return this;
        }

        public User build() {
            if (this.user.userId == null) {
                throw new PropertyUserVisibleException(OBJECT_NAME, "/userId", PropertyUserVisibleException.MSG_NULL_NOT_ALLOWED);
            }
            if (this.user.password == null) {
                throw new PropertyUserVisibleException(OBJECT_NAME, "/password", PropertyUserVisibleException.MSG_NULL_NOT_ALLOWED);
            }
            if (this.user.koreanName != null) {
                validateKoreanName(this.user.koreanName);
            }
            if (this.user.phoneNumber != null) {
                validatePhoneNumber(this.user.phoneNumber);
            }
            if (this.user.email != null) {
                validateEmail(this.user.email);
            }
            if (this.user.department != null) {
                validateDepartment(this.user.department);
            }
            validateUserId(this.user.userId);

            return this.user;
        }

    }

    @Column(name = COL_NAME_USER_ID, nullable = false)
    @Getter
    @ToString.Include
    private String userId;
    @Column(length = 60, nullable = false)
    @Getter
    private String password;
    @ToString.Include
    private String koreanName;
    @ToString.Include
    private String phoneNumber;
    @ToString.Include
    private String email;
    @ToString.Include
    private String department;

    public Optional<String> getKoreanName() {
        return Optional.ofNullable(this.koreanName);
    }

    public Optional<String> getPhoneNumber() {
        return Optional.ofNullable(this.phoneNumber);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(this.email);
    }

    public Optional<String> getDepartment() {
        return Optional.ofNullable(this.department);
    }

    public void patchPassword(String password) {
        this.password = password;
    }

    public void patchDepartment(@Nullable String department) {
        if (department != null) {
            validateDepartment(department);
        }
        this.department = department;
    }

    public void patchKoreanName(@Nullable String koreanName) {
        if (koreanName != null) {
            validateKoreanName(koreanName);
        }
        this.koreanName = koreanName;
    }

    public void patchPhoneNumber(@Nullable String phoneNumber) {
        if (phoneNumber != null) {
            validatePhoneNumber(phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    public void patchEmail(@Nullable String email) {
        if (email != null) {
            validateEmail(email);
        }
        this.email = email;
    }

}
