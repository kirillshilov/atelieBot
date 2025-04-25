package com.sarataza.atelieBot.Service.Admin;

import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.MessageAdminAndUser;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
@Service
@Slf4j
@RequiredArgsConstructor
public class UtilityAdminService {
    private final AdminService adminService;
    private final AdminKeyboardFactory adminKeyboardFactory;
    private final OrderConteiner orderConteiner;
    private final UserService userService;
    public SendMessage toGeneral(Update update) {
        Long chatID;
        if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatID = update.getMessage().getChatId();
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general_admin");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }
    public SendMessage initializeMailing(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        AdminEntity temp = admin.get();
        temp.setState("initMailing");
        adminService.updateAdmin(temp);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.cancelKeyboard());
        sendMessage.setText("Введите текст сообщения для рассылки. При нажатии на значок отправки текст нельзя будет изменить. Он сразу же будет отправлен всем пользователям");
        sendMessage.setChatId(chatID);
        return sendMessage;
    }
    public SendMessage toGeneralWithOrder(Update update) {
        Long chatID;
        if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatID = update.getMessage().getChatId();
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general_admin");
        adminService.updateAdmin(adminTemp);
        orderConteiner.setStaticOrder(new OrderEntity());  ;
        return sendMessage;
    }
    public MessageAdminAndUser doMailing(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        if (query != null && query.equals("/cancel")) {
            return new MessageAdminAndUser(toGeneral(update), null, null);
        }
        if (!update.hasMessage()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelKeyboard());
            sendMessage.setText("Произошла ошибка. Проверьте текст. И повторите отправку");
            sendMessage.setChatId(chatID);
            return new MessageAdminAndUser(sendMessage, null, null);
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(update.getMessage().getText());
        MessageAdminAndUser messageAdminAndUser = new MessageAdminAndUser(toGeneral(update), sendMessage, userService.getAllAppUser());
        return messageAdminAndUser;
    }
}
