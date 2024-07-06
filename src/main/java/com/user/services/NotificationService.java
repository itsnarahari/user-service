package com.user.services;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.user.config.ProviderConfiguration;
import com.user.dto.CommunicationTypes;
import com.user.dto.NotificationStatus;
import com.user.entities.Notification;
import com.user.entities.User;
import com.user.repository.NotificationRepository;
import com.user.utils.GenericCacheUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;
    @Value("${twilio.account.auth_token}")
    private String AUTH_TOKEN;
    @Value("${twilio.account.from}")
    private String FROM;

    @Autowired
    private Handlebars handlebars;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    ProviderConfiguration providerConfiguration;

    @Autowired
    NotificationRepository notificationRepository;

    String smsTemplate = "Hi %s, Your One Time Password %s for api maker and its expires in 10 mins";

    @Value("${app.auth.resetPasswordLink}")
    private String resetPasswordLink;

    @Async
    public void email(String email, String actionType, User user) {
        Notification notification = null;
        try {
            notification = prepareNotificationAndSave(null, null, CommunicationTypes.EMAIL, email,
                    NotificationStatus.IN_PROGRESS, actionType, email, null,
                    providerConfiguration.getUsername(), user);

            Map<String, Object> templateData = new HashMap<>();
            String name = email;

            if(user!=null){
                name = "%s %s".formatted(user.getFirstName(), user.getLastName()).trim();
                name = !name.isBlank()?name:user.getUsername();
            }
            templateData.put("actionType", actionType);
            templateData.put("expiresIn", 10);
            templateData.put("regards", "Nextlevel");

            templateData.put("name", name);
            templateData.put("logoUrl", providerConfiguration.getLogoUrl());

            String templateString = "";
            if(actionType.equalsIgnoreCase("RESET_PASSWORD")){

                String uuid = UUID.randomUUID().toString();
                GenericCacheUtil.putValue(uuid, user.getUsername());
                String resetUrl = "%s?token=%s".formatted(resetPasswordLink, uuid);

                templateData.put("link", resetUrl);
                Template template = handlebars.compile("resetPasswordLink-template");
                templateString = template.apply(templateData);
            } else if(actionType.equalsIgnoreCase("PASSWORD_CHANGE_SUCCESS")){

                Template template = handlebars.compile("resetPasswordLink-template");
                templateString = template.apply(templateData);
            }
            else{
                int otp = otpService.generateOTP(email);
                log.info("Generated OTP {}", otp);
                Template template = handlebars.compile("otp-template");
                templateData.put("otp", otp);
                templateString = template.apply(templateData);
                log.info("Email: {} and OTP: {}", email, otp);
            }
            providerConfiguration.setSubject("Reset Password Link");
            boolean isSent = sendEmail(new String[]{email}, templateString, providerConfiguration.getUsername(),
                    providerConfiguration.getFromName(), providerConfiguration.getSubject());

            if (isSent) {
                notification.setNotificationStatus(NotificationStatus.SENT);
                updateNotification(notification);
            }else {
                notification.setNotificationStatus(NotificationStatus.FAILED);
                updateNotification(notification);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Got error {}", ex.getMessage());
            assert notification != null;
            notification.setNotificationStatus(NotificationStatus.FAILED);
            notification.setErrorDescription(ex.getMessage());
            updateNotification(notification);
        }
    }

    public boolean sendEmail(String[] to, String body, String from, String fromName, String subject) throws Exception {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(from, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setSentDate(new Date());

        boolean isSent = false;
        try {
            emailSender.send(mimeMessage);
            isSent = true;
        } catch (Exception ex) {
            log.error("Sending e-mail error: {}", ex.getMessage());
        }
        return isSent;
    }

    @Async
    public void sms(String mobileNumber, String actionType, User user) {

        int otp = otpService.generateOTP(mobileNumber);
        Notification notification = null;
        try {
            notification = prepareNotificationAndSave(null, null, CommunicationTypes.EMAIL, mobileNumber,
                    NotificationStatus.IN_PROGRESS, actionType, mobileNumber, null,
                    providerConfiguration.getUsername(), user);
            log.info("mobileNumber: {} and OTP: {}", mobileNumber, otp);
            boolean isSent = sendSMS(mobileNumber, String.format(smsTemplate, mobileNumber, otp));

            if (isSent) {
                notification.setNotificationStatus(NotificationStatus.SENT);
                updateNotification(notification);
            }else {
                notification.setNotificationStatus(NotificationStatus.FAILED);
                updateNotification(notification);
            }
        }  catch (RuntimeException e) {
            log.error("Got error {}", e.getMessage());
            assert notification != null;
            notification.setNotificationStatus(NotificationStatus.FAILED);
            notification.setErrorDescription(e.getMessage());
            updateNotification(notification);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendSMS(String toPhnNumber, String body){

        boolean isSent = false;
        try{
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message message = Message.creator(
                            new com.twilio.type.PhoneNumber(toPhnNumber),
                            new com.twilio.type.PhoneNumber(FROM),
                            body)
                    .create();
            log.info("response {}", message.getStatus().name());
            isSent = true;
        }catch (Exception ignored){
            log.info(String.valueOf(ignored));
        }

        return isSent;
    }

    public Notification prepareNotificationAndSave(Long notificationId, Long userId, CommunicationTypes communicationTypes,
                                                   String sentTo, NotificationStatus notificationStatus, String action,
                                                   String username, String errorDescription, String from, User user) {
        log.info("Preparing notification for {}", sentTo);

        if (userId == null || userId == 0) {
            userId = !ObjectUtils.isEmpty(user) ? user.getUserId() : null;
        }

        Notification notification = new Notification();
        notification.setNotificationId(notificationId);
        notification.setUserId(userId);
        notification.setCommunicationTypes(communicationTypes);
        notification.setSentTo(sentTo);
        notification.setNotificationStatus(notificationStatus);
        notification.setAction(action);
        notification.setErrorDescription(errorDescription);
        return updateNotification(notification);

    }

    public Notification updateNotification(Notification notification) {

        log.info("Updating the notification for Id {}", notification.getNotificationId());
        notification = notificationRepository.save(notification);
        log.info("Notification Id: {} Is Updated ", notification.getNotificationId());
        return notification;
    }

}
