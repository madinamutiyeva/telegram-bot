package com.cryptobot.service.Impl;

import com.cryptobot.enums.UserState;
import com.cryptobot.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements UserStateService {
    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    @Override
    public void setUserState(Long chatId, UserState state) {
        userStates.put(chatId, state);
    }

    @Override
    public UserState getUserState(Long chatId) {
        return userStates.getOrDefault(chatId, UserState.NONE);
    }

    @Override
    public void clearUserState(Long chatId) {
        userStates.remove(chatId);
    }
}
