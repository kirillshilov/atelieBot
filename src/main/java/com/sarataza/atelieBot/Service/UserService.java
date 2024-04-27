package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<AppUserEntity> getAppUserByLogin(Long chatID) {
        return userRepository.getAppUserEntitiesByLogin(chatID);
    }

    public AppUserEntity updateUser(AppUserEntity appUserEntity) {
        return userRepository.save(appUserEntity);
    }

    public Optional<AppUserEntity> getAppUserByPhone(String phone) {
        return userRepository.getAppUserEntitiesByPhone(phone);
    }
    public void deleteUser(Long id){
         userRepository.deleteById(id);
    }
    public List<AppUserEntity> getAllAppUser(){
        return userRepository.findAll();
    }

    public String getUserToStringByIdForAdmin(Long id) {
        Optional<AppUserEntity> appUser = userRepository.findById(id);
        StringBuilder stringBuilder = new StringBuilder();
        if (!appUser.isPresent()){
            return "";
        }
        if (appUser.get().getPhone() != null){
            stringBuilder.append(" тел: ").append(appUser.get().getPhone());
        }
        if (appUser.get().getLastName() != null){
            stringBuilder.append(" Фамилия: ").append(appUser.get().getLastName());
        }
        if (appUser.get().getFirstName() != null){
            stringBuilder.append(" Имя: ").append(appUser.get().getFirstName());
        }
        return stringBuilder.toString();
    }
}
