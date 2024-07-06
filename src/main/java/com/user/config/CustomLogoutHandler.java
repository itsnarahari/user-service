package com.user.config;

import com.user.entities.User;
import com.user.entities.UserSession;
import com.user.repository.UserRepository;
import com.user.repository.UserSessionRepository;
import com.user.security.TokenServiceProvider;
import com.user.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private TokenServiceProvider tokenServiceProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            log.info("logout handler started");
            Optional<Cookie> token = CookieUtils.getCookie(request, "token");
            Optional<Cookie> sessionId = CookieUtils.getCookie(request, "signedInSessionId");

            if(token.isPresent() && sessionId.isPresent()) {

                log.info("SESSION ID {} Is about to logout", sessionId.get().getValue());

                // Extract username from the token
                String username = tokenServiceProvider.extractUserName(token.get().getValue());
                Optional<User> byUsername = userRepository.findByUsername(username);

                if (byUsername.isPresent()) {
                    User user = byUsername.get();
                    UserSession userSession = userSessionRepository.getUserSessionByUserIdAndSessionId(user.getUserId(), sessionId.get().getValue());
                    if(!ObjectUtils.isEmpty(userSession)){
                        userSession.setSignedOutAt(LocalDateTime.now());
                        // Update the user's logout time
                        userSessionRepository.save(userSession);
                        log.info("SESSION ID {} signed out successfully", sessionId.get().getValue());
                        log.info("USER Id {} signed out successfully", user.getUsername());
                    }else{
                        log.info("User Session not found or already updated with the signed out time");
                    }

                }else{
                    log.info("User not found");
                }
            }else{
                log.info("token not found in the request cookies");
            }
            // Clear the JWT token from cookies
            Cookie cookie = new Cookie("token", null);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setAttribute("loggedInSessionId", null);
            cookie.setMaxAge(0); // Expire the cookie
            response.addCookie(cookie);

            // Invalidate the session
            log.info("Signed out successfully");
            } catch (Exception e) {
            throw new RuntimeException("Error during logout", e);
        }
    }
}
