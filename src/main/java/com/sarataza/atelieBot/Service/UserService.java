package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<AppUserEntity> getAppUserByLogin(Long chatID) {
        return userRepository.getAppUserEntitiesByLogin(chatID);
    }
    public AppUserEntity updateUser (AppUserEntity appUserEntity){
        return userRepository.save(appUserEntity);
    }
    public Optional<AppUserEntity> getAppUserByPhone(String phone) {
        return userRepository.getAppUserEntitiesByPhone(phone);
    }
}
