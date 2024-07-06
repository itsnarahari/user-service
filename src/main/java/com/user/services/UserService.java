package com.user.services;

import com.user.dto.ApiResponse;
import com.user.dto.PasswordChangeDto;
import com.user.entities.User;
import com.user.entities.UserSession;
import com.user.security.UserPrincipal;

public interface UserService {

    public User getUser(UserPrincipal userPrincipal);
    public UserSession getUserSession(Long userId, String sessionId);
    public UserSession updateUserSession(UserSession userSession);

    public UserSession getLastSignedDetails(Long userId);

    public User getUserByUserName(String username);
    public ApiResponse passwordChange(UserPrincipal userPrincipal, PasswordChangeDto passwordChangeDto);

}
