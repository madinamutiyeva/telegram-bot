package com.cryptobot.bot.command;

import com.cryptobot.enums.UserState;
import com.cryptobot.model.Subscriber;
import com.cryptobot.service.SubscribeService;
import com.cryptobot.service.UserStateService;
import com.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand implements IBotCommand {
    private final SubscribeService subscribeService;
    private final UserStateService userStateService;
    private final TextUtil textUtil;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long chatId = message.getChatId();
        SendMessage answer = new SendMessage();
        answer.setChatId(String.valueOf(chatId));
        answer.setText("Введите цену, на которую хотите подписаться (например: 83878.100)");
        userStateService.setUserState(chatId, UserState.AWAITING_PRICE);
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception", e);
        }
    }

    public void onUpdateReceived(Update update, AbsSender absSender) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info(chatId + ": " + message);
            try {
                handlePriceInput(absSender, chatId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handlePriceInput(AbsSender absSender, Long chatId, String message) {
        try {
            Double price = Double.parseDouble(message);

            subscribeService.getSubscriberByChatId(chatId).ifPresentOrElse(
                    subscriber -> textUtil.sendTextMessage(absSender, chatId, "Вы уже подписаны. Напишите /unsubscribe, чтобы отменить подписку."),
                    () -> {
                        subscribeService.addSubscriber(chatId, price);
                        textUtil.sendTextMessage(absSender, chatId, "Вы подписались на уведомления при цене: " + price);
                    }
            );
            userStateService.setUserState(chatId, UserState.NONE);
        } catch (NumberFormatException e) {
            textUtil.sendTextMessage(absSender, chatId, "Некорректный формат цены. Введите число (например: 83878.100)");
        }
    }




}