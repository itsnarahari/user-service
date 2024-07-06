package com.user.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "UserSession")
@Table(name = "USER_SESSION")
@Getter
@Setter
@NoArgsConstructor
public class UserSession {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_SESSION_ID")
    private String userSessionId;

    @Column(name = "SIGNED_AT")
    private LocalDateTime signedAt;

    @Column(name = "SIGNED_OUT_AT")
    private LocalDateTime signedOutAt;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

}
