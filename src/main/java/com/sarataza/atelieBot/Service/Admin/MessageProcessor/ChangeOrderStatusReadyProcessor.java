package com.sarataza.atelieBot.Service.Admin.MessageProcessor;

import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.Admin.AdminKeyboardFactory;
import com.sarataza.atelieBot.Service.Admin.AdminService;
import com.sarataza.atelieBot.Service.Admin.UtilityAdminService;
import com.sarataza.atelieBot.Service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;
@Slf4j
@Service ("change_order_status_ready")
@RequiredArgsConstructor
public class ChangeOrderStatusReadyProcessor implements AdminAbstractMessageProcessor{
    private final OrderService orderService;
    private final AdminService adminService;
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
        if (query != null && query.equals("/cancel")) {
            return utilityAdminService.toGeneral(update);
        }
        Long orderNumber;
        try {
            orderNumber = Long.valueOf(update.getMessage().getText());
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelKeyboard());
            sendMessage.setText("Неверный номер заказа. Номер должен содержать только цифры. Введите другой номер заказа ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        Optional<OrderEntity> order = orderService.findOrderByNumber(orderNumber);
        if (!order.isPresent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelKeyboard());
            sendMessage.setText("Заказ с таким номером не найден. Введите другой номер заказа ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        } else {
            Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
            OrderEntity orderEntity = order.get();
            if (orderEntity.getDone() == null) {
                orderEntity.setDone(false);
            } else orderEntity.setDone(!orderEntity.getDone());
            orderService.saveOrder(orderEntity);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
            sendMessage.setText("Статус заказа изменен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general_admin");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        }
    }
}
