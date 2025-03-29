package com.cryptobot.bot.command;

import com.cryptobot.enums.UserState;
import com.cryptobot.service.SubscribeService;
import com.cryptobot.service.UserStateService;
import com.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SetLimitCommand implements IBotCommand {
    private final SubscribeService subscribeService;
    private final UserStateService userStateService;
    private final TextUtil textUtil;

    @Override
    public String getCommandIdentifier() {
        return "set_limit";
    }

    @Override
    public String getDescription() {
        return "Устанавливает лимиты на уведомления";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        SendMessage answer = new SendMessage();
        Long chatId = message.getChatId();
        answer.setChatId(String.valueOf(chatId));
        answer.setText("Введите лимит на отправку уведомлений в день (например: 5)");
        userStateService.setUserState(chatId, UserState.AWAITING_SMS_LIMIT);
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update, AbsSender absSender) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            try {
                handleLimitInput(absSender, chatId, update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleLimitInput(AbsSender absSender, Long chatId, Update update) {
        try {
            Integer limit = Integer.parseInt(update.getMessage().getText());
            subscribeService.getSubscriberByChatId(chatId).ifPresentOrElse(
                    subscriber -> {
                        subscribeService.addSmsLimit(chatId, limit);
                        textUtil.sendTextMessage(absSender, chatId, "Установленный лимит обновлен до: " + limit);
                    },
                                () ->
                                    textUtil.sendTextMessage(absSender, chatId, "Для установления лимита нужно офрмить подписку используйте команду /subscribe")
                    );
            userStateService.setUserState(chatId, UserState.NONE);
        } catch (NumberFormatException e) {
            textUtil.sendTextMessage(absSender, chatId, "Некорректный формат цены. Введите число (например: 5)");
        }
    }
}
