package com.cryptobot.service.Impl;

import com.cryptobot.bot.CryptoBot;
import com.cryptobot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final CryptoBot cryptoBot;
    public void sendNotification(Long chatId, String message) {
        cryptoBot.sendNotification(chatId, message);
    }
}
