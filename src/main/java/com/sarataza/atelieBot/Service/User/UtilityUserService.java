package com.sarataza.atelieBot.Service.User;

import com.sarataza.atelieBot.Model.AppUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UtilityUserService {
    private final UserService userService;
    public SendMessage toGeneral(Update update) {
        Long chatID;
        if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatID = update.getMessage().getChatId();
        }
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AppUserEntity temp;
        temp = appUserEntity.orElseGet(AppUserEntity::new);
        temp.setState("general_user");
        userService.updateUser(temp);
        return sendMessage;
    }
}
