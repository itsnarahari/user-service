package com.user.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "PHONES")
@Entity
@Setter
@Getter
@ToString
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PHONE_ID")
    private Long phoneId;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "PHONE_COUNTRY_CODE")
    private String phoneCountryCode;

    @Column(name = "IS_PRIMARY")
    private Boolean primary;

    @Column(name = "VERIFIED")
    private Boolean verified;

    @Column(name = "BAD")
    private Boolean bad;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID")
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
