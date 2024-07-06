package com.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.config.AppProperties;
import com.user.dto.*;
import com.user.entities.*;
import com.user.exception.ApplicationException;
import com.user.exception.ValidationException;
import com.user.repository.*;
import com.user.security.CurrentUser;
import com.user.security.TokenServiceProvider;
import com.user.security.UserPrincipal;
import com.user.utils.CookieUtils;
import com.user.utils.GenericCacheUtil;
import com.user.utils.UserUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenServiceProvider tokenServiceProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SignupRequestRepository signupRequestRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserService userService;

    @Value("${app.auth.secretKey:your-secret-key}")
    private String secretKey;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    @Override
    @Transactional
    public ApiResponse signupVerifyAndSave(UserDto userDto) {

        if (!otpService.isInSession(userDto.getUsername())) {
            throw new ValidationException("Something went wrong, Please try again!");
        }


        otpService.verifyOtp(userDto.getUsername(), userDto.getOtp());
        otpService.clearOTP(userDto.getUsername());

        String decryptedPassword = UserUtility.decryptPassword(userDto.getPassword(), secretKey);
        userDto.setPassword(null);

        // Creating user account
        User user = objectMapper.convertValue(userDto, User.class);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : AuthProvider.local);
        user.setVerified(true);
        user.setActive(true);
        user.setProviderId("0");
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(decryptedPassword));
        user.setRoles(userDto.getRoles() != null ? userDto.getRoles() : Set.of(roleRepository.findByName("USER")));

        try {
            user = userRepository.save(user);
            if (!CollectionUtils.isEmpty(user.getEmails())) {
                List<Email> emailList = new ArrayList<>();
                User finalUser = user;
                user.getEmails().forEach(email -> {
                    email.setUser(finalUser);
                    email.setVerified(true);
                    email.setBad(false);
                    emailList.add(email);
                });
                emailRepository.saveAll(emailList);
            }

            if (!CollectionUtils.isEmpty(user.getPhones())) {
                List<Phone> emailList = new ArrayList<>();
                User finalUser = user;
                user.getPhones().forEach(phone -> {
                    phone.setUser(finalUser);
                    phone.setVerified(true);
                    phone.setBad(false);
                    phone.setPrimary(true);
                    emailList.add(phone);
                });
                phoneRepository.saveAll(emailList);
            }
        } catch (Exception ex) {
            throw new ValidationException("Got an error: %s".formatted(ex.getMessage()));
        }
        return new ApiResponse(true, "Your Successfully Registered, Please login", HttpStatus.OK.value());
    }

    @Override
    @Transactional
    public AuthResponse signIn(LoginRequest loginRequest, HttpServletResponse response, HttpSession session) {

        loginRequest.setPassword(UserUtility.decryptPassword(loginRequest.getPassword(), secretKey));
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication {}", authentication.isAuthenticated());
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ValidationException("User %s not found".formatted(loginRequest.getUsername())));
            String loggedInSessionId = UUID.randomUUID().toString();

            log.info("Logged In user session id: {}", loggedInSessionId);
            String token = tokenGenerator(user);
            Cookie sessionCookie = new Cookie("signedInSessionId", loggedInSessionId);
            sessionCookie.setPath("/");
            sessionCookie.setHttpOnly(true);
            sessionCookie.setAttribute("token", token);
            sessionCookie.setMaxAge(18000); // 30 minutes
            response.addCookie(sessionCookie);

            // To Track user
            updateActivityTracker(user.getUserId(), loggedInSessionId);
            UserDto userDto = objectMapper.convertValue(user, UserDto.class);
            UserSession lastSignedDetails = userService.getLastSignedDetails(user.getUserId());
            if(!ObjectUtils.isEmpty(lastSignedDetails)){
                userDto.setLastSignedInAt(lastSignedDetails.getSignedAt());
                userDto.setLastSignedOutAt(lastSignedDetails.getSignedOutAt());
            }

            return new AuthResponse(token, userDto);
        } catch (BadCredentialsException ex){
            throw new ValidationException("Invalid username or password!: %s".formatted(ex.getMessage()));
        } catch (Exception ex) {
            log.error("Got error {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() ->
        new BadCredentialsException("User not found: %s".formatted(userName)));
        return UserPrincipal.create(user, objectMapper.convertValue(user, Map.class));
    }

    @Transactional
    @Override
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ValidationException("User id %d not found".formatted(id))
        );
        return UserPrincipal.create(user, objectMapper.convertValue(user, Map.class));
    }

    @Override
    public ApiResponse sendOtp(UserDto userDto) {

        // Save request for history or future purpose.
        signupRequest(userDto);

        User user = userRepository.checkIfUserExistAndIsVerified(userDto.getUsername());
        if(!ObjectUtils.isEmpty(user)){
            throw new ValidationException("User %s is already exist, Please try again with different username".formatted(userDto.getUsername()));
        }

//        boolean isSent = false;
        if (userDto.getCommunicationTypes().name().equalsIgnoreCase(CommunicationTypes.EMAIL.name())) {
            notificationService.email(userDto.getUsername(), userDto.getForWhat(), user);
        } else if (userDto.getCommunicationTypes().name().equalsIgnoreCase(CommunicationTypes.PHONE.name())) {
            notificationService.sms(userDto.getUsername(), userDto.getForWhat(), user);
        } else {
            log.info("Communication Type {} Is Invalid", userDto.getCommunicationTypes().name());
            throw new ValidationException("Communication Type Is Invalid");
        }

//        if (isSent) {
//            return new ApiResponse(true, String.format("OTP has been sent to %s and Valid for 10 minutes",
//                    otpDto.getEmailOrMobile()), HttpStatus.OK.value());
//        } else {
//            throw new ApplicationException("OTP Not sent!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
//        }

        return new ApiResponse(true, String.format("OTP has been sent to %s and Valid for 10 minutes",
                userDto.getUsername()), HttpStatus.OK.value());
    }

    @Override
    @Async
    public void signupRequest(UserDto userDto) {

        log.info("signupRequest running for username: {}", userDto.getUsername());
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setFirstName(userDto.getFirstName());
        signupRequest.setLastName(userDto.getLastName());
        signupRequest.setUserId(userDto.getUserId());
        signupRequest.setUsername(userDto.getUsername());
        signupRequest.setProvider(AuthProvider.user);
        signupRequest.setCommunicationTypes(userDto.getCommunicationTypes());
        signupRequestRepository.save(signupRequest);
        log.info("signupRequest completed for username: {}", userDto.getUsername());

    }

    @Override
    @Async
    public void updateSignupRequest(User user) {
        Optional<SignupRequest> byUsername = signupRequestRepository.findByUsername(user.getUsername());
        if (!byUsername.isPresent()) {
            SignupRequest signupRequest = byUsername.get();
            signupRequestRepository.save(signupRequest);
        }
    }

    @Override
    public ApiResponse forgot(UserDto userDto, HttpServletResponse response) {

        User user = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() ->
                    new ValidationException("User not found with : %s".formatted(userDto.getUsername())));

        String communicationType=userDto.getCommunicationTypes().name();
        String communication=userDto.getUsername();

        Optional<Email> emails = user.getEmails().stream().filter(Email::getPrimary).findFirst();
        Optional<Phone> phones = user.getPhones().stream().filter(Phone::getPrimary).findFirst();

        if(emails.isPresent()){
            communication = emails.get().getEmail();
            communicationType = "EMAIL";
        }else if(phones.isPresent()){
            communication = phones.get().getPhone();
            communicationType = "PHONE";
        }

        // boolean isSent = false;
        if (communicationType.equalsIgnoreCase(CommunicationTypes.EMAIL.name())) {
            notificationService.email(communication, userDto.getForWhat(), user);
        } else if (userDto.getCommunicationTypes().name().equalsIgnoreCase(CommunicationTypes.PHONE.name())) {
            notificationService.sms(communication, userDto.getForWhat(), user);
        } else {
            log.info("Communication Type {} Is Invalid", userDto.getCommunicationTypes().name());
            throw new ValidationException("Communication Is Invalid");
        }
        return new ApiResponse(true, "The %s has been sent to: %s".formatted(communicationType, communication), HttpStatus.OK.value());
    }

    @Override
    @Transactional
    public ApiResponse signOut(@CurrentUser UserPrincipal userPrincipal, HttpServletRequest httpServletRequest) {
        try{
            User user = userService.getUser(userPrincipal);
            if(!ObjectUtils.isEmpty(user)){
                Cookie sessionId = CookieUtils.getCookie(httpServletRequest, "SESSIONID").get();
                UserSession userSession = userService.getUserSession(user.getUserId(), sessionId.getValue());
                if(!ObjectUtils.isEmpty(userSession)){
                    userSession.setSignedOutAt(LocalDateTime.now());
                    userService.updateUserSession(userSession);
                }
            }
            return new ApiResponse(true, "successfully logged out", 200);
        }catch (Exception ex){
            throw new ApplicationException(ex.getMessage(), 500, ex);
        }

    }

    @Override
    public ApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        try {
            String username = GenericCacheUtil.getValue(updatePasswordRequest.getToken());
            if (username == null) {
                throw new ValidationException("Session has been expired, Please try again");
            }

            Optional<User> byUsername = userRepository.findByUsername(username);


            if (byUsername.isEmpty()) {
                throw new ValidationException("User not found, Please try again");
            }
            String decryptedPassword = UserUtility.decryptPassword(updatePasswordRequest.getNewPassword(), secretKey);
            updatePasswordRequest.setNewPassword(null);
            User user = byUsername.get();
            user.setPassword(passwordEncoder.encode(decryptedPassword)); // In a real application, hash the password before saving
            userRepository.save(user);
            GenericCacheUtil.deleteValue(updatePasswordRequest.getToken());
            return new ApiResponse(true, "Password changed successfully", 200);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ValidationException("Something went wrong, Please try again %s".formatted(ex.getMessage()));
        }
    }
    private String tokenGenerator(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("username", user.getUsername());
        map.put("imageUrl", user.getImageUrl());
        map.put("verified", user.getVerified());
        map.put("name", "%s %s".formatted(user.getFirstName(), user.getLastName()).trim());
        return tokenServiceProvider.generateToken(map).getToken();
    }

    private void updateActivityTracker(Long userId, String sessionId) {
        log.info("LoggedIn User {} and sessionId {}", userId, sessionId);
        UserSession userSession = new UserSession();
        userSession.setUserId(userId);
        userSession.setUserSessionId(sessionId);
        userSession.setSignedAt(LocalDateTime.now());
        userSession = userSessionRepository.save(userSession);
        log.info("UserSessionId {} got Updated", userSession.getUserSessionId());
    }
}
