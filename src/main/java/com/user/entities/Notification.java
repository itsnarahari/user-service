package com.user.entities;

import com.user.dto.NotificationStatus;
import com.user.dto.CommunicationTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "NOTIFICATIONS")
@Setter@Getter
@NoArgsConstructor
@Entity
public class Notification{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long notificationId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "NOTIFICATION_TYPE")
    @Enumerated(EnumType.STRING)
    private CommunicationTypes communicationTypes;

    @Column(name = "SENT_TO")
    private String sentTo;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "ERROR_DESCRIPTION")
    private String errorDescription;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;
}
