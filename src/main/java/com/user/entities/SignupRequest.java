package com.user.entities;

import com.user.dto.AuthProvider;
import com.user.dto.CommunicationTypes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "SignupRequest")
@Table(name = "SIGNUP_REQUEST")
@Setter@Getter
@AllArgsConstructor@NoArgsConstructor
public class SignupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SIGNUP_REQUEST_ID")
    private Long signupRequestId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "USERNAME")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROVIDER")
    private AuthProvider provider;

    @Column(name = "PROVIDER_ID")
    private String providerId;

    @Column(name="COMMUNICATION_TYPES")
    @Enumerated(EnumType.STRING)
    private CommunicationTypes communicationTypes;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

}
