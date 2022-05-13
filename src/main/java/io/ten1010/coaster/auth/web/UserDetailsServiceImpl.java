package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.dao.UserRepository;
import io.ten1010.coaster.auth.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = this.userRepository.findAllByUserId(username, Pageable.unpaged()).toList();
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("No exist");
        }
        if (users.size() != 1) {
            throw new RuntimeException();
        }
        User found = users.get(0);

        return new UserDetailsImpl(found.getUserId(), found.getPassword());
    }

}
