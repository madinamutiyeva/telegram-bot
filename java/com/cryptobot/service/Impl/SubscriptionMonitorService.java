package com.cryptobot.service.Impl;

import com.cryptobot.model.Subscriber;
import com.cryptobot.repository.SubscribersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionMonitorService {
    private final SubscribersRepository subscribersRepository;
    private final CryptoCurrencyService cryptoCurrencyService;
    private final NotificationServiceImpl notificationServiceImpl;

    @Scheduled(fixedRate = 3_600_000)//1h - 3_600_000
    public void checkSubscribedPrice() {
        double bitcoinPrice = getBitcoinPrice();
        log.info("Checking subscribed currencies...");
//        double bitcoinPrice = 22;
        if (bitcoinPrice == 0.0) {
            return;
        }
        for (Subscriber subscriber : getSubscriptions(bitcoinPrice)) {
            if (subscriber == null) return;
            Double tgtPrice = subscriber.getPrice();
            Integer smsLimit = subscriber.getSmsLimit();
            Integer smsCount = subscriber.getSmsCount();
            if (smsLimit != null && smsCount != null && smsLimit > smsCount) {
                notificationServiceImpl.sendNotification(subscriber.getChatId(),
                        "Текущая цена биткоина: " + bitcoinPrice + ". Это ниже вашего целевого значения: " + tgtPrice + ".");
                subscriber.setSmsCount(subscriber.getSmsCount() + 1);
                subscribersRepository.save(subscriber);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailySmsCount() {
        log.info("Сброс счетчика SMS для всех подписчиков.");
        List<Subscriber> subscribers = subscribersRepository.findAll();
        for (Subscriber subscriber : subscribers) {
            subscriber.setSmsCount(0);
        }
        subscribersRepository.saveAll(subscribers);
    }

    private double getBitcoinPrice() {
        try {
            return cryptoCurrencyService.getBitcoinPrice();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error getting bitcoin price");
        }
        return 0;
    }

    private List<Subscriber> getSubscriptions(double bitcoinPrice) {
        List<Subscriber> subscribers = subscribersRepository.findSubscribersWithPriceLessThan(bitcoinPrice);
        return subscribers;
    }
}
