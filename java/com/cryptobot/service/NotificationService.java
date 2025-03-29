package com.cryptobot.service;

public interface NotificationService {
    void sendNotification(Long chatId, String message);
}
