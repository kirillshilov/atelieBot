package com.sarataza.atelieBot.Service.User.MessageProcessor;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Service.User.UserKeyboardFactory;
import com.sarataza.atelieBot.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
@Service("error")
@RequiredArgsConstructor
@Slf4j
public class ErrorUserMessageProcessor implements UserAbstractMessageProcessor{
    private final UserService userService;
    @Override
    public SendMessage proceed(Update update) {
        log.warn("error update {}", update);
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Произошла ошибка. Выберете действие: ");
        sendMessage.setChatId(chatID);
        AppUserEntity temp = appUserEntity.get();
        temp.setState("general_user");
        userService.updateUser(temp);
        return sendMessage;
    }
}
