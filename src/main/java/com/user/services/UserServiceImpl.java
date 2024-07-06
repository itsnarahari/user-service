package com.user.services;

import com.user.dto.ApiResponse;
import com.user.dto.PasswordChangeDto;
import com.user.entities.User;
import com.user.entities.UserSession;
import com.user.exception.ValidationException;
import com.user.repository.UserRepository;
import com.user.repository.UserSessionRepository;
import com.user.security.UserPrincipal;
import com.user.utils.UserUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Value("${app.auth.secretKey:your-secret-key}")
    private String secretKey;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User getUser(UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ValidationException("User Id: %d not found".formatted(userPrincipal.getId())));
    }

    @Override
    public UserSession getUserSession(Long userId, String sessionId) {
        return userSessionRepository.getUserSessionByUserIdAndSessionId(userId, sessionId);
    }

    @Override
    public UserSession updateUserSession(UserSession userSession) {
        return userSessionRepository.save(userSession);
    }

    @Override
    public UserSession getLastSignedDetails(Long userId) {
        return userSessionRepository.getLastSignedDetails(userId);
    }

    @Override
    public User getUserByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public ApiResponse passwordChange(UserPrincipal userPrincipal, PasswordChangeDto passwordChangeDto) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ValidationException("User Id: %d not found".formatted(userPrincipal.getId())));
        String oldPassword = UserUtility.decryptPassword(passwordChangeDto.getOldPassword(), secretKey);
        String newPassword = UserUtility.decryptPassword(passwordChangeDto.getNewPassword(), secretKey);

        if(ObjectUtils.isEmpty(user)){
            throw new ValidationException("User not found");
        }

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return new ApiResponse(true, "Password changed successfully", HttpStatus.OK.value());
        } else {
            throw new ValidationException("Old password is incorrect");
        }
    }
}
