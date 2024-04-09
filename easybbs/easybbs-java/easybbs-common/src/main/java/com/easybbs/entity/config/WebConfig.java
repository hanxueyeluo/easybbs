package com.easybbs.entity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebConfig extends AppConfig{

    @Value("${spring.mail.username}")
    private String sendUserName;

    @Value("${admin.emails}")
    private String adminEmails;

    public String getSendUserName() {
        return sendUserName;
    }

    public String getAdminEmails() {
        return adminEmails;
    }
}
