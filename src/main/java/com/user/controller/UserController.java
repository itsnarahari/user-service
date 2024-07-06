package com.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.dto.ApiResponse;
import com.user.dto.PasswordChangeDto;
import com.user.dto.UserDto;
import com.user.entities.User;
import com.user.entities.UserSession;
import com.user.security.CurrentUser;
import com.user.security.UserPrincipal;
import com.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/me")
    public UserDto getCurrentUser(@CurrentUser UserPrincipal userPrincipal, HttpServletRequest httpServletRequest) {
        User user = userService.getUser(userPrincipal);
        UserDto userDto = objectMapper.convertValue(user, UserDto.class);
        UserSession lastSignedDetails = userService.getLastSignedDetails(user.getUserId());
        if(!ObjectUtils.isEmpty(lastSignedDetails)){
            userDto.setLastSignedInAt(lastSignedDetails.getSignedAt());
            userDto.setLastSignedOutAt(lastSignedDetails.getSignedOutAt());
        }
            return userDto;
    }

    @PostMapping("/passwordChange")
    public ResponseEntity<ApiResponse> passwordChange(@CurrentUser UserPrincipal userPrincipal, @RequestBody @Valid PasswordChangeDto passwordChangeDto){
        return ResponseEntity.status(HttpStatus.OK).body(userService.passwordChange(userPrincipal, passwordChangeDto));
    }
}
