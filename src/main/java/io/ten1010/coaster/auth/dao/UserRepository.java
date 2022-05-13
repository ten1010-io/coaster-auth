package io.ten1010.coaster.auth.dao;

import io.ten1010.coaster.auth.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByUserId(String userId, Pageable pageable);

    Page<User> findAllByKoreanName(String username, Pageable pageable);

    Page<User> findAllByPhoneNumber(String phoneNumber, Pageable pageable);

    Page<User> findAllByEmail(String email, Pageable pageable);

    Page<User> findAllByDepartment(String department, Pageable pageable);

}
