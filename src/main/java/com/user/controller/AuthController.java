package com.user.controller;

import com.user.dto.*;
import com.user.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody @Valid UserDto userDto) throws Exception {
        return ResponseEntity.ok(authService.sendOtp(userDto));
    }
    @PostMapping(value = "/signup/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> signupVerifyAndSave(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.signupVerifyAndSave(userDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response, HttpSession session) {
        System.out.println(loginRequest);
        return ResponseEntity.ok(authService.signIn(loginRequest, response, session));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid UserDto userDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.forgot(userDto, response));
    }

//    @PostMapping("/signOut")
//    public ResponseEntity<ApiResponse> signOut(@CurrentUser UserPrincipal userPrincipal, HttpServletRequest httpServletRequest) {
//        return ResponseEntity.ok(authService.signOut(userPrincipal, httpServletRequest));
//    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.updatePassword(updatePasswordRequest));
    }
}
