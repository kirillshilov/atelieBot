package com.sarataza.atelieBot.Service.User;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Util.PhoneEncryptor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PhoneEncryptor phoneEncryptor;

    public UserService(UserRepository userRepository, PhoneEncryptor phoneEncryptor) {
        this.userRepository = userRepository;
        this.phoneEncryptor = phoneEncryptor;
    }

    @SneakyThrows
    public Optional<AppUserEntity> getAppUserByLogin(Long chatID) {
        Optional<AppUserEntity> user = userRepository.getAppUserEntitiesByLogin(chatID);
        if (user.isPresent() && user.get().getPhone() != null) {
            user.get().setPhone(phoneEncryptor.decrypt(user.get().getPhone()));
        }
        return user;
    }

    @SneakyThrows
    public AppUserEntity updateUser(AppUserEntity appUserEntity) {
        if (appUserEntity.getPhone() != null) {
            appUserEntity.setPhone(phoneEncryptor.encrypt(appUserEntity.getPhone()));
        }
        return userRepository.save(appUserEntity);
    }

    @SneakyThrows
    public Optional<AppUserEntity> getAppUserByPhone(String phone) {
        phone = phoneEncryptor.encrypt(phone);

        Optional<AppUserEntity> user = userRepository.getAppUserEntitiesByPhone(phone);
        if (user.isPresent() && user.get().getPhone() != null) {
            user.get().setPhone(phoneEncryptor.decrypt(user.get().getPhone()));
        }
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @SneakyThrows
    public List<AppUserEntity> getAllAppUser() {
        List<AppUserEntity> users = userRepository.findAll();
        for (AppUserEntity appUserEntity : users) {

            appUserEntity.setPhone(phoneEncryptor.decrypt(appUserEntity.getPhone()));

        }
        return userRepository.findAll();
    }

    @SneakyThrows
    public String getUserToStringByIdForAdmin(Long id) {
        Optional<AppUserEntity> appUser = userRepository.findById(id);
        StringBuilder stringBuilder = new StringBuilder();
        if (!appUser.isPresent()) {
            return "";
        }
        if (appUser.get().getPhone() != null) {
            stringBuilder.append(" тел: ").append(phoneEncryptor.decrypt(appUser.get().getPhone()));
        }
        if (appUser.get().getLastName() != null) {
            stringBuilder.append(" Фамилия: ").append(appUser.get().getLastName());
        }
        if (appUser.get().getFirstName() != null) {
            stringBuilder.append(" Имя: ").append(appUser.get().getFirstName());
        }
        return stringBuilder.toString();
    }
}
