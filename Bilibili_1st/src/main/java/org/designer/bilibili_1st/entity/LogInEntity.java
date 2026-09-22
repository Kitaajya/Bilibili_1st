package org.designer.bilibili_1st.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bilibili_user_log_in")
public class LogInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;

    @Column(name = "realName")
    private String realName;

    @Column(name = "virtualName")
    private String virtualName;

    @Column(name = "qq_email")
    @JsonProperty("qq_email")
    private String qqEmail;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "role")
    private String role;

    @Column(name="avatar")
    private String avatar;

}