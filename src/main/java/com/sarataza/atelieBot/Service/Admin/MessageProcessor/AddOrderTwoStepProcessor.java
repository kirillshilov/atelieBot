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

import java.util.Optional;

@Service("add_order_step_two")
@Slf4j
@RequiredArgsConstructor
public class AddOrderTwoStepProcessor implements AdminAbstractMessageProcessor{

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
        String orderWorks;
        try {
            orderWorks = update.getMessage().getText();
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboard());
            sendMessage.setText("Проверьте вводимые работы и попробуйте еще раз ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        OrderEntity order = orderConteiner.getStaticOrder();
        order.setWorks(orderWorks);
        orderConteiner.setStaticOrder(order);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboardPlusWithoutNumber());
        sendMessage.setText("Введите номер телефона в формате 79203658495 ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("add_order_step_three");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }
}
