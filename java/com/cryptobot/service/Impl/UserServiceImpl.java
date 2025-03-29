package com.cryptobot.service.Impl;

import com.cryptobot.model.User;
import com.cryptobot.repository.UserRepository;
import com.cryptobot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    public void registerUser(User user) {

    }
}
