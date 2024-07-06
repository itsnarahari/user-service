package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtpDto {

    private String emailOrMobile;
    private CommunicationTypes communicationTypes;
    private Long otp;
    private String forWhat;
}
