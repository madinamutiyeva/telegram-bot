package com.cryptobot.bot.command;

import com.cryptobot.model.Subscriber;
import com.cryptobot.service.SubscribeService;
import com.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {
    private final SubscribeService subscribeService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    @SneakyThrows
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId().toString());
        try {
            Long chatId = message.getChatId();
            Optional<Subscriber> subscription = subscribeService.getSubscriberByChatId(chatId);
            answer.setText("Текущая подписка биткоина: " + subscription.get().getPrice());
            absSender.execute(answer);
        }
        catch (Exception e) {
            answer.setText("У вас нет подписки");
            absSender.execute(answer);
        }
    }
}