package com.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class UserTokenDetails {

    private Long userId;
    private String username;
    private String imageUrl;
    private String verified;

}
