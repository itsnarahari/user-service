package com.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter@Getter
@AllArgsConstructor
public class TokenDetailDtoResponse {

    private String accessToken;
    private String scope;
    private String tokenType;
    private String expiresIn;
    private String refreshToken;
}
