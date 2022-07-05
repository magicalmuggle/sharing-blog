package com.github.magicalmuggle.blog.service;

import com.github.magicalmuggle.blog.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Map<String, String> userPasswords = new ConcurrentHashMap<>();

    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("zhangsan", "zhangsan");
    }

    public void save(String username, String password) {
        userPasswords.put(username, bCryptPasswordEncoder.encode(password));
    }

    public String getPassword(String userName) {
        return userPasswords.get(userName);
    }

    public User getUserById(Integer id) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!userPasswords.containsKey(username)) {
            throw new UsernameNotFoundException(username + " 不存在！");
        }

        String encodedPassword = userPasswords.get(username);

        return new org.springframework.security.core.userdetails.User(
                username, encodedPassword, Collections.emptyList());
    }
}
