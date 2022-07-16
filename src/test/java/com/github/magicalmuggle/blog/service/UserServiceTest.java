package com.github.magicalmuggle.blog.service;

import com.github.magicalmuggle.blog.entity.User;
import com.github.magicalmuggle.blog.dao.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserService userService;

    @Test
    public void save() {
        // 调用 userService
        // 验证 userService将请求转发给了 userMapper
        when(bCryptPasswordEncoder.encode("myPassword")).thenReturn("myEncryptedPassword");

        userService.save("myUsername", "myPassword");

        verify(userMapper).insertUser("myUsername", "myEncryptedPassword");
    }

    @Test
    public void getUserByUsername() {
        userService.getUserByUsername("myUsername");

        verify(userMapper).findUserByUsername("myUsername");
    }

    @Test
    public void throwExceptionWhenUserNotFound() {
        // 没有配置的情况下默认返回 null，所以下面这行代码是多余的
        // when(mockUserMapper.findUserByUsername("myUsername")).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("myUsername"));
    }

    @Test
    public void returnUserDetailsWhenUserFound() {
        when(userMapper.findUserByUsername("myUsername"))
                .thenReturn(new User(123, "myUsername", "myEncodedPassword"));

        UserDetails userDetails = userService.loadUserByUsername("myUsername");

        Assertions.assertEquals("myUsername", userDetails.getUsername());
        Assertions.assertEquals("myEncodedPassword", userDetails.getPassword());
    }
}
