package com.user.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "EMAILS")
@Setter
@Getter
@Entity
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMAIL_ID")
    private Long emailId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "IS_PRIMARY")
    private Boolean primary=false;

    @Column(name = "VERIFIED")
    private Boolean verified=false;

    @Column(name = "BAD")
    private Boolean bad=false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "updated_by")
    private String updatedBy;

}
