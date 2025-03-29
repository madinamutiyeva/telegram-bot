package com.cryptobot.service;

import com.cryptobot.enums.UserState;

public interface UserStateService {
    void setUserState(Long chatId, UserState state);
    UserState getUserState(Long chatId);
    void clearUserState(Long chatId);
}
