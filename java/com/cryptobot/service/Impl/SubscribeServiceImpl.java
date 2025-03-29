package com.cryptobot.service.Impl;

import com.cryptobot.model.Subscriber;
import com.cryptobot.repository.SubscribersRepository;
import com.cryptobot.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SubscribeServiceImpl implements SubscribeService {
    private final SubscribersRepository subscribersRepository;

    @Override
    public List<Subscriber> getSubscribers() {
        return subscribersRepository.findAll();
    }


    @Override
    public void addSubscriber(Long chatId, Double price) {
        String userUUID = UUID.randomUUID().toString();

        Subscriber subscriber = new Subscriber();

        subscriber.setChatId(chatId);
        subscriber.setUserUUID(userUUID);
        subscriber.setPrice(price);
        subscriber.setSmsLimit(3);
        subscriber.setSmsCount(0);

        subscribersRepository.save(subscriber);
    }

    @Override
    public Optional<Subscriber> getSubscriberByChatId(Long userId) {
        return subscribersRepository.findByChatId(userId);
    }

    @Override
    @Transactional
    public void deleteSubscriberByChatId(Long userId) {
        subscribersRepository.deleteByChatId(userId);
    }

    @Override
    public void addSmsLimit(Long chatId, Integer limit) {
        getSubscriberByChatId(chatId).ifPresent(subscriber -> {
            subscriber.setSmsLimit(limit);
            subscribersRepository.save(subscriber);
        });
    }


}
