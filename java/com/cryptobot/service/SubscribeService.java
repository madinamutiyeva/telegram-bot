package com.cryptobot.service;

import com.cryptobot.model.Subscriber;

import java.util.List;
import java.util.Optional;

public interface SubscribeService {
    List<Subscriber> getSubscribers();
    void addSubscriber(Long chatId, Double price);
    Optional<Subscriber> getSubscriberByChatId(Long chatId);
    void deleteSubscriberByChatId(Long chatId);

    void addSmsLimit(Long chatId, Integer limit);
}
