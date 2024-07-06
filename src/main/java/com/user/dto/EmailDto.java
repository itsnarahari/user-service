package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {


    private Long emailId;

    private String email;

    private boolean primary;

    private boolean verified;

    private boolean bad;

    private UserDto user;

    private LocalDateTime createdOn;
    private String createdBy;
    private LocalDateTime updatedOn;
    private String updatedBy;

}
