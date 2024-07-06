package com.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter
@ToString
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
    private Date date;

}
