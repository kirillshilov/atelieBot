package com.sarataza.atelieBot.Service.User.MessageProcessor;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Repository.OrderRepository;
import com.sarataza.atelieBot.Service.OrderService;
import com.sarataza.atelieBot.Service.User.UserService;
import com.sarataza.atelieBot.Service.User.UtilityUserService;
import com.sarataza.atelieBot.Util.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service("send_contact")
@RequiredArgsConstructor
@Slf4j
public class AddContactUserMessageProcessor implements UserAbstractMessageProcessor{
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderService orderService;
    private final PhoneService phoneService;
    private final UtilityUserService utilityUserService;
    @Override
    public SendMessage proceed(Update update) {
        log.warn("send_contact update {}", update);
        Long chatId = update.getMessage().getChatId();
        AppUserEntity appUser = new AppUserEntity();
        if(!update.hasMessage() || !update.getMessage().hasContact()){
            return utilityUserService.toGeneral(update);
        }
        String phone = update.getMessage().getContact().getPhoneNumber();
        phone = phoneService.formatPhone(phone);
        List<OrderEntity> orderEntities = new ArrayList<>();
        Optional<AppUserEntity> user = userService.getAppUserByPhone(phone);
        appUser = userService.getAppUserByLogin(chatId).get();
        if (user.isPresent() &&
                user.get().getId() != appUser.getId()){
            orderEntities = orderRepository.getOrderEntitiesByAppUserEntityId(user.get().getId());
            for (OrderEntity order: orderEntities) {
                order.setAppUserEntity(appUser);
                log.warn(orderService.saveOrder(order).toString());
            }
            userService.deleteUser(user.get().getId());
        }
        StringBuilder text = new StringBuilder();
        appUser.setState("general_user");
        appUser.setPhone(phone);
        appUser.setLogin(update.getMessage().getChatId());
        appUser.setFirstName(update.getMessage().getContact().getFirstName());
        appUser.setLastName(update.getMessage().getContact().getLastName());
        userService.updateUser(appUser);
        SendMessage sendMessage = utilityUserService.toGeneral(update);
        List<OrderEntity> orderList = orderService.getAllOrderByUserId(appUser.getId());
        if (orderList.isEmpty()) {
            text.append("Заказы не найдены. Попробуйте найти заказ по номеру из квитанции ");
        } else {
            text.append(orderService.getOrderByStringForUser(orderList));
        }
        sendMessage.setText(text.toString());
        return sendMessage;
    }
}
