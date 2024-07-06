package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter@Getter
@AllArgsConstructor@NoArgsConstructor
public class PhoneDto {


    private Long phoneId;

    private String phone;

    private String phoneCountryCode;

    private boolean isPrimary;

    private boolean isVerified;

    private boolean isBad;

    private UserDto user;

    private LocalDateTime createdOn;

    private String createdBy;

    private LocalDateTime updatedOn;

    private String updatedBy;
}
