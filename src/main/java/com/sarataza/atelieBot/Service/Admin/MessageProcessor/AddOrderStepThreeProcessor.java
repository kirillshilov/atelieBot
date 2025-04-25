package com.sarataza.atelieBot.Service.Admin.MessageProcessor;

import com.sarataza.atelieBot.Exception.PhoneFormatException;
import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Service.Admin.AdminKeyboardFactory;
import com.sarataza.atelieBot.Service.Admin.AdminService;
import com.sarataza.atelieBot.Service.Admin.OrderConteiner;
import com.sarataza.atelieBot.Service.Admin.UtilityAdminService;
import com.sarataza.atelieBot.Service.OrderService;
import com.sarataza.atelieBot.Service.User.UserService;
import com.sarataza.atelieBot.Util.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Service("add_order_step_three")
@RequiredArgsConstructor
@Slf4j
public class AddOrderStepThreeProcessor implements AdminAbstractMessageProcessor {
    private final OrderService orderService;
    private final UserService userService;
    private final AdminService adminService;
    private final PhoneService phoneService;
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
        String phone;
        if (query != null && query.equals("/without_number")) {
            orderService.saveOrder(orderConteiner.getStaticOrder());
            orderConteiner.setStaticOrder(new OrderEntity()) ;
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен. без номера телефона ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general_admin");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        }
        try {
            phone = phoneService.formatPhone(update.getMessage().getText());
        } catch (PhoneFormatException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboardPlusWithoutNumber());
            sendMessage.setText("Введен неверный номер телефона. Введите номер в формате 79208746587 ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.cancelWithOrderKeyboardPlusWithoutNumber());
            sendMessage.setText("Произошла ошибка. Введите номер в формате 79208746587 ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByPhone(phone);
        if (appUserEntity.isPresent()) {
            orderConteiner.getStaticOrder().setAppUserEntity(appUserEntity.get());
            orderService.saveOrder(orderConteiner.getStaticOrder());
            orderConteiner.setStaticOrder(new OrderEntity());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general_admin");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        } else {
            AppUserEntity appUser = new AppUserEntity();
            appUser.setPhone(phone);
            userService.updateUser(appUser);
            orderConteiner.getStaticOrder().setAppUserEntity(appUser);
            orderService.saveOrder(orderConteiner.getStaticOrder());
            orderConteiner.setStaticOrder(new OrderEntity());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(adminKeyboardFactory.getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general_admin");
            adminService.updateAdmin(adminTemp);
            return sendMessage;

        }
    }
}
