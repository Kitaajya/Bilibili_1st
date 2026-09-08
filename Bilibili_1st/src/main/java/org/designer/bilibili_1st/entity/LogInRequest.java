package org.designer.bilibili_1st.entity;

import lombok.Data;

@Data
public class LogInRequest {
    private String qqEmail;
    private String phoneNumber;
    private String password;
    private String realName;
    private String virtualName;
}