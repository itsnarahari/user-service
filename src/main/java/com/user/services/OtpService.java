package com.user.services;

import com.user.exception.ValidationException;
import com.user.utils.GenericCacheUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@Transactional
public class OtpService {

    //This method is used to push the opt number against Key. Rewrite the OTP if it exists
    //Using user id  as key
    public int generateOTP(String key) {

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        GenericCacheUtil.putValue(key, otp);
        return otp;
    }

    //This method is used to return the OTP number against Key->Key values is username
    public int getOtp(String key) {
        try {
            return (int) GenericCacheUtil.getValue(key);
        } catch (Exception e) {
            return 0;
        }
    }


    public boolean isInSession(String emailOrMobile) {
        int sessionOtp = getOtp(emailOrMobile);
        if (sessionOtp > 0) {
            return true;
        }
        return false;
    }

    public String verifyOtp(String emailOrMobile, Long otp) {
        int sessionOtp = getOtp(emailOrMobile);
        if (sessionOtp != otp) {
            throw new ValidationException("One time password invalid or expired. Try again!");
        }
        return "success";
    }

    public void clearOTP(String key) {
        GenericCacheUtil.deleteValue(key);
    }

}
