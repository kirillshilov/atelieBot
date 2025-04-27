package com.sarataza.atelieBot.Service.User.MessageProcessor;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.OrderService;
import com.sarataza.atelieBot.Service.User.UserKeyboardFactory;
import com.sarataza.atelieBot.Service.User.UserService;
import com.sarataza.atelieBot.Service.User.UtilityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;
@Service("add_order")
@RequiredArgsConstructor
@Slf4j
public class AddOrderUserMessageProcessor implements UserAbstractMessageProcessor{
    private final UtilityUserService utilityUserService;
    private final UserService userService;
    private final OrderService orderService;
    @Override
    public SendMessage proceed(Update update) {
        log.warn("add_order update {}",update);
        if (update.hasCallbackQuery() && Objects.equals(update.getCallbackQuery().getData(), "/cancel")){
            return utilityUserService.toGeneral(update);
        }
        Long chatId = update.getMessage().getChatId();
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(UserKeyboardFactory.getCancelKeyboard());
        String orderNum = update.getMessage().getText();
        sendMessage.setChatId(chatId);
        Long orderNumInt;
        try {
            orderNumInt = Long.valueOf(orderNum);
        } catch (Exception e) {
            sendMessage.setText("Неверно введен номер. Попробуйте ввести еще раз ");
            return sendMessage;
        }
        Optional<OrderEntity> order = orderService.findOrderByNumber(orderNumInt);
        if (order.isPresent()) {
            AppUserEntity temp = appUserEntity.get();
            OrderEntity orderEntity = order.get();
            orderEntity.setAppUserEntity(temp);
            orderService.saveOrder(orderEntity);
            temp.setState("general_user");
            userService.updateUser(temp);
            sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(UserKeyboardFactory.getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен. Его можно найти в МОИ ЗАКАЗЫ ");
            sendMessage.setChatId(chatId);
            return sendMessage;
        }
        else {
            sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(UserKeyboardFactory.getCancelKeyboard());
            sendMessage.setText("Заказ не найден. Проверьте номер заказа ");
            sendMessage.setChatId(chatId);
            return sendMessage;
        }
    }
}
