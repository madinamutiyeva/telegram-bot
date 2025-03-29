package com.cryptobot.bot.command;

import com.cryptobot.service.Impl.SubscribeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UnsubscribeCommand implements IBotCommand {
    private final SubscribeServiceImpl subscribeService;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId.toString());
        try {
            subscribeService.getSubscriberByChatId(chatId).ifPresentOrElse(
                    subscriber -> {
                        subscribeService.deleteSubscriberByChatId(chatId);
                        answer.setText("Подписка отменена");
                    },
                    () -> answer.setText("Вы еще не подписаны. Напишите /subscribe, чтобы подписаться.")
            );
            absSender.execute(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}