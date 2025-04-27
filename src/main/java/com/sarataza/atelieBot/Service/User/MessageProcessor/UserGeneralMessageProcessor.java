package com.sarataza.atelieBot.Service.User.MessageProcessor;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.OrderService;
import com.sarataza.atelieBot.Service.User.UserKeyboardFactory;
import com.sarataza.atelieBot.Service.User.UserService;
import com.sarataza.atelieBot.Service.User.UtilityUserService;
import com.sarataza.atelieBot.Util.CONSTANTS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;
@Service("general_user")
@RequiredArgsConstructor
@Slf4j
public class UserGeneralMessageProcessor implements UserAbstractMessageProcessor{

    private final UserService userService;
    private final OrderService orderService;
    private final UtilityUserService utilityUserService;

    @Override
    public SendMessage proceed(Update update) {
        log.warn("general update {}",update);
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatID);
        StringBuilder text = new StringBuilder();
        if (query == null){
            return utilityUserService.toGeneral(update);
        }
        switch (query) {
            case "/orders" -> {
                List<OrderEntity> orderList = orderService.getAllOrderByUserId(appUserEntity.get().getId());
                if (orderList.isEmpty()) {
                    text.append("Заказы не найдены. ");
                    if (appUserEntity.get().getPhone() == null) {
                        text.append("Для поиска заказов нажмите ОТПРАВИТЬ КОНТАКТ или ПОИСК ЗАКАЗА. ");
                    }
                } else {
                    text.append(orderService.getOrderByStringForUser(orderList));
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                return sendMessage;
            }
            case "/faq" -> {
                return faq(update);
            }
            case "/cancel" -> {
                return utilityUserService.toGeneral(update);
            }
            case "/add_order" -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(UserKeyboardFactory.getCancelKeyboard());
                sendMessage.setText("Введите номер заказа из квитанции ");
                sendMessage.setChatId(chatID);
                AppUserEntity appUser = appUserEntity.get();
                appUser.setState("add_order");
                userService.updateUser(appUser);
                return sendMessage;
            }
            case "/send_contact" -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(UserKeyboardFactory.getCancelKeyboard());
                sendMessage.setText("Для отправки контактных данных нажмите кнопку ОТПРАВИТЬ КОНТАКТ." +
                        " Отправляя номер телефона вы соглашаетесь на обработку персональных данных " +
                        "данные сохраняются только для поиска заказов и не передаются третьим лицам. " +
                        " Если вы не хотите предоставлять свой номер телефона то можете найти заказ по его номеру ");
                sendMessage.setChatId(chatID);
                sendMessage.setReplyMarkup(UserKeyboardFactory.getContactKeyboard());
                AppUserEntity appUser = appUserEntity.get();
                appUser.setState("send_contact");
                userService.updateUser(appUser);
                log.warn(appUser.toString());
                return sendMessage;
            }
            default -> utilityUserService.toGeneral(update);

        }
        return utilityUserService.toGeneral(update);
    }
    public SendMessage faq(Update update) {
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText(CONSTANTS.FAQ);
        sendMessage.setChatId(chatID);
        return sendMessage;
    }
}
