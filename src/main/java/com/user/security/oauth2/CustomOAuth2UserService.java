package com.user.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.entities.Email;
import com.user.entities.Role;
import com.user.exception.OAuth2AuthenticationProcessingException;
import com.user.dto.AuthProvider;
import com.user.entities.User;
import com.user.repository.EmailRepository;
import com.user.repository.RoleRepository;
import com.user.repository.UserRepository;
import com.user.security.UserPrincipal;
import com.user.security.oauth2.user.OAuth2UserInfo;
import com.user.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(StringUtils.isEmpty(oAuth2UserInfo.getUserName())) {
            throw new OAuth2AuthenticationProcessingException("Username not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByUsername(oAuth2UserInfo.getUserName());

        User user = null;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            setAuthentication(oAuth2UserInfo);
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            setAuthentication(oAuth2UserInfo);
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setUsername(oAuth2UserInfo.getUserName());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        user.setVerified(oAuth2UserInfo.getEmailVerified());
        user.setActive(true);
        user.setRoles(Set.of(roleRepository.findByName("USER")));
        user = userRepository.save(user);

        if(StringUtils.hasText(oAuth2UserInfo.getEmail())){
            Email email = new Email();
            email.setEmail(oAuth2UserInfo.getEmail());
            email.setVerified(oAuth2UserInfo.getEmailVerified());
            email.setBad(false);
            email.setPrimary(true);
            email.setUser(user);
            emailRepository.save(email);
            user.setEmails(List.of(email));
        }
        return user;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setVerified(oAuth2UserInfo.getEmailVerified());
        return userRepository.save(existingUser);
    }

    private void setAuthentication(OAuth2UserInfo oAuth2UserInfo){

        User user = new User();
        user.setProviderId(oAuth2UserInfo.getProviderId());
        user.setUsername(oAuth2UserInfo.getUserName());
        user.setFirstName(oAuth2UserInfo.getFirstName());
        user.setLastName(oAuth2UserInfo.getLastName());
        user.setProvider(oAuth2UserInfo.getAuthProvider());
        user.setRoles(Set.of(oAuth2UserInfo.getRole()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        context.setAuthentication(usernamePasswordAuthenticationToken);
        SecurityContextHolder.setContext(context);
    }

}
