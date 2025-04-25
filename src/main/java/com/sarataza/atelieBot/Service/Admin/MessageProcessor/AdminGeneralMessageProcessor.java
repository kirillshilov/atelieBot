package com.sarataza.atelieBot.Service.Admin.MessageProcessor;

import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.Admin.AdminKeyboardFactory;
import com.sarataza.atelieBot.Service.Admin.AdminService;
import com.sarataza.atelieBot.Service.Admin.UtilityAdminService;
import com.sarataza.atelieBot.Service.OrderService;
import com.sarataza.atelieBot.Service.User.MessageProcessor.UserAbstractMessageProcessor;
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

@Service("general_admin")
@RequiredArgsConstructor
@Slf4j
public class AdminGeneralMessageProcessor implements AdminAbstractMessageProcessor {
private final AdminService adminService;
private final OrderService orderService;
private final AdminKeyboardFactory adminKeyboardFactory;
private final UtilityAdminService utilityAdminService;

    @Override
    public SendMessage proceed(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        StringBuilder text = new StringBuilder();
        switch (query) {
            case "/active_order" -> {
                List<OrderEntity> orderList = orderService.getAllActiveOrder();
                if (orderList.isEmpty()) {
                    text.append("Активные заказы не найдены ");
                } else {
                    text.append(orderService.getOrderByStringForAdmin(orderList));
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                return sendMessage;
            }
            case "/change_order_status" -> {
                text.append("Введите номер заказа ");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(adminKeyboardFactory.cancelKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                AdminEntity adminTemp = admin.get();
                adminTemp.setState("change_order_status_ready");
                adminService.updateAdmin(adminTemp);
                return sendMessage;
            }
            case "/add_order" -> {
                text.append("Для добавления заказа введите его номер ");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                AdminEntity adminTemp = admin.get();
                adminTemp.setState("add_order_begin");
                adminService.updateAdmin(adminTemp);
                return sendMessage;
            }
            case "/mailing" -> {
                return utilityAdminService.initializeMailing(update);
            }
            case "/cancel" -> {
                return utilityAdminService.toGeneral(update);
            }
            default -> {
                return error(chatID);
            }

}

}
    public SendMessage error(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
        sendMessage.setText("Произошла ошибка. Выберете действие: ");
        sendMessage.setChatId(chatId);
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatId);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general_admin");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }
}
