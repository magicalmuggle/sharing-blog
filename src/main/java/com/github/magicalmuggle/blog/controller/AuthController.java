package com.github.magicalmuggle.blog.controller;

import com.github.magicalmuggle.blog.entity.LoginResult;
import com.github.magicalmuggle.blog.service.AuthService;
import com.github.magicalmuggle.blog.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @Inject
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          AuthService authService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @GetMapping("/auth")
    @ResponseBody
    public LoginResult auth() {
        return authService.getCurrentUser()
                .map(LoginResult::success)
                .orElse(LoginResult.success("用户未登录", false));
    }

    @PostMapping("/auth/register")
    @ResponseBody
    public LoginResult register(@RequestBody Map<String, String> usernameAndPassword, HttpServletRequest request) {
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");

        if (username == null || password == null) {
            return LoginResult.failure("用户名或密码为空");
        }
        if (username.length() < 1 || username.length() > 15) {
            return LoginResult.failure("用户名格式不正确");
        }
        if (password.length() < 6 || password.length() > 16) {
            return LoginResult.failure("密码格式不正确");
        }

        try {
            userService.save(username, password);
        } catch (DuplicateKeyException e) {
            return LoginResult.failure("用户名已存在");
        }
        // 注册后自动登录
        login(usernameAndPassword, request);
        return LoginResult.success("注册成功", userService.getUserByUsername(username));
    }

    @PostMapping("/auth/login")
    @ResponseBody
    public LoginResult login(@RequestBody Map<String, String> usernameAndPassword, HttpServletRequest request) {
        // 反爬虫
        if (request.getHeader("user-agent") == null
                || !request.getHeader("user-agent").contains("Mozilla")) {
            return LoginResult.failure("禁止爬虫");
        }

        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");

        // 让 spring 来鉴权
        // 尝试从数据库获取该 username 对应的 User 的真正信息，结合传入的 password 生成鉴权 token
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return LoginResult.failure("用户不存在");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            // 拿着 token 开始鉴权，如果密码错误会抛 BadCredentialsException 异常
            authenticationManager.authenticate(token);
            // 鉴权成功后更新当前用户的认证信息，设置 Cookie
            SecurityContextHolder.getContext().setAuthentication(token);

            return LoginResult.success("登录成功", userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            // 密码错误，鉴权失败
            return LoginResult.failure("密码不正确");
        }
    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public LoginResult logout() {
        LoginResult result = authService.getCurrentUser()
                .map(user -> LoginResult.success("注销成功", false))
                .orElse(LoginResult.failure("用户未登录"));
        SecurityContextHolder.clearContext();
        return result;
    }
}
