package com.sarataza.atelieBot.Service.User.MessageProcessor;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Service.User.UserKeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service("registration")
@RequiredArgsConstructor
public class RegistrationUserMessageProcessor implements UserAbstractMessageProcessor {
    private final UserRepository userRepository;

    @Override
    public SendMessage proceed(Update update) {
        log.warn("registration update {}", update);
        AppUserEntity appUserEntity = new AppUserEntity();
        if (update.getMessage() != null && update.getMessage().getChat() != null&&update.getMessage().getChatId() != null) {
            appUserEntity.setFirstName(update.getMessage().getChat().getFirstName());
            appUserEntity.setLastName(update.getMessage().getChat().getLastName());
            appUserEntity.setLogin(update.getMessage().getChatId());
            appUserEntity.setLastName(update.getMessage().getChat().getLastName());
        } else {
            appUserEntity.setLogin(update.getCallbackQuery().getMessage().getChatId());
        }
        appUserEntity.setState("general_user");
        userRepository.save(appUserEntity);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Вы успешно прошли регистрацию. Для поиска заказов нажмите ОТПРАВИТЬ КОНТАКТ или ПОИСК ЗАКАЗА. ");
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }
}
