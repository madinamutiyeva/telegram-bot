package com.cryptobot.bot;

import com.cryptobot.bot.command.SetLimitCommand;
import com.cryptobot.bot.command.SubscribeCommand;
import com.cryptobot.enums.UserState;
import com.cryptobot.service.UserStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.List;


@Service
@Slf4j
public class CryptoBot extends TelegramLongPollingCommandBot {
    private final SubscribeCommand subscribeCommand;
    private final String botUsername;
    private final UserStateService userStateService;
    private final SetLimitCommand setLimitCommand;


    public CryptoBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<IBotCommand> commandList,
            SubscribeCommand subscribeCommand,
            UserStateService userStateService,
            SetLimitCommand setLimitCommand) {
        super(botToken);
        this.botUsername = botUsername;
        this.subscribeCommand = subscribeCommand;
        this.userStateService = userStateService;
        commandList.forEach(this::register);
        this.setLimitCommand = setLimitCommand;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        UserState userState = userStateService.getUserState(update.getMessage().getChatId());
        switch (userState) {
            case AWAITING_PRICE ->
                    subscribeCommand.onUpdateReceived(update, this);
            case AWAITING_SMS_LIMIT ->
                    setLimitCommand.onUpdateReceived(update, this);
            default -> log.error("Unknown user state: {}", userState);
        }
    }

    public void sendNotification(Long chatId, String message) {
        SendMessage answer = new SendMessage();
        answer.setChatId(String.valueOf(chatId));
        answer.setText(message);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }

}
