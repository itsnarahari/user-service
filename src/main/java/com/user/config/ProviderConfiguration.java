package com.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "email")
@Setter
@Getter
public class ProviderConfiguration {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private Boolean debug;
    private String subject;
    private String fromName;
    private String logoUrl;

}