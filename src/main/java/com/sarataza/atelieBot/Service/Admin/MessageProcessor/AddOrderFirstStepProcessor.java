package com.sarataza.atelieBot.Service.Admin.MessageProcessor;

import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.Admin.AdminKeyboardFactory;
import com.sarataza.atelieBot.Service.Admin.AdminService;
import com.sarataza.atelieBot.Service.Admin.OrderConteiner;
import com.sarataza.atelieBot.Service.Admin.UtilityAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service("add_order_begin")
@RequiredArgsConstructor
public class AddOrderFirstStepProcessor implements AdminAbstractMessageProcessor{
    private final AdminService adminService;
    private final AdminKeyboardFactory adminKeyboardFactory;
    private final UtilityAdminService utilityAdminService;
    private final OrderConteiner orderConteiner;

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
        if (query != null && query.equals("/cancelWithOrder")) {
            return utilityAdminService.toGeneralWithOrder(update);
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        Long orderNumber;
        try {
            orderNumber = Long.valueOf(update.getMessage().getText());
        } catch (Exception e) {
            log.warn(e.getMessage());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboard());
            sendMessage.setText("Неверный номер заказа. Номер должен содержать только цифры. Введите другой номер заказа ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        OrderEntity order = new OrderEntity();
        order.setDone(false);
        order.setLocalDate(LocalDate.now().toString());
        order.setNumber(orderNumber);
        orderConteiner.setStaticOrder(order);
        log.warn(orderConteiner.getStaticOrder().toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboard());
        sendMessage.setText("Введите работы по данному заказу ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("add_order_step_two");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }
}
