package com.user.services;

import com.user.dto.*;
import com.user.entities.User;
import com.user.security.CurrentUser;
import com.user.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    ApiResponse signupVerifyAndSave(UserDto userDto);

    AuthResponse signIn(LoginRequest loginRequest, HttpServletResponse response, HttpSession session);

    ApiResponse sendOtp(UserDto userDto) throws Exception;


    UserDetails loadUserByUsername(String userName);

    UserDetails loadUserById(Long id);
    void signupRequest(UserDto userDto);

    void updateSignupRequest(User user);

    ApiResponse forgot(UserDto userDto, HttpServletResponse response);
    public ApiResponse signOut(@CurrentUser UserPrincipal userPrincipal, HttpServletRequest httpServletRequest);
    public ApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);

}
