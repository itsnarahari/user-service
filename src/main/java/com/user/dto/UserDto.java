package com.user.dto;

import com.user.entities.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long userId;

    private String firstName;

    private String lastName;
    private String fullName;

    @NotBlank
    @Size(min = 6, max = 50, message  = "username should be In between 6 and 50 characters")
    private String username;
    List<EmailDto> emails;

    List<PhoneDto> phones;

    private String imageUrl;

    private Boolean verified;
    private Boolean active;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private LocalDateTime createdOn;

    private String createdBy;

    private LocalDateTime updatedOn;
    private LocalDateTime lastSignedInAt;
    private LocalDateTime lastSignedOutAt;
    private String updatedBy;

    private Set<Role> roles;

    private Boolean authRequired;

    private Boolean emailOrMobile;

    private String forWhat;

    private CommunicationTypes communicationTypes;

    private Long phoneCountryCode;
    private Long otp;

    public String getFullName() {
        this.fullName= "%s %s".formatted(this.firstName, this.lastName).trim();
        return this.fullName;
    }

}
