package com.simple.bank.udemy.notification.service;

import com.simple.bank.udemy.auth.entity.UserEntity;
import com.simple.bank.udemy.notification.dto.NotificationDTO;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, UserEntity user);
}
