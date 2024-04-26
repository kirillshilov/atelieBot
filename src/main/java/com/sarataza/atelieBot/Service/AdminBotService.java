package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Exception.PhoneFormatException;
import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminBotService {
    private final OrderService orderService;
    private final UserService userService;
    private final AdminService adminService;
    private OrderEntity staticOrder = new OrderEntity();



    public SendMessage general(Update update) {
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
                sendMessage.setReplyMarkup(getGeneralKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                return sendMessage;
            }
            case "/change_order_status" -> {
                text.append("Введите номер заказа ");
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(cancelKeyboard());
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
                sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                AdminEntity adminTemp = admin.get();
                adminTemp.setState("add_order_begin");
                adminService.updateAdmin(adminTemp);
                return sendMessage;
            }
            case "/cancel" -> {
                return toGeneral(update);
            }
            default -> {
                return
                        error(chatID);
            }
        }

    }

    public SendMessage addOrderFirstStep(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        if (query != null && query.equals("/cancelWithOrder")) {
            return toGeneralWithOrder(update);
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        Integer orderNumber;
        try {
            orderNumber = Integer.valueOf(update.getMessage().getText());
        } catch (Exception e) {
            log.info(e.getMessage());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
            sendMessage.setText("Неверный номер заказа. Номер должен содержать только цифры. Введите другой номер заказа ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        OrderEntity order = new OrderEntity();
        order.setDone(false);
        order.setNumber(orderNumber);
        this.staticOrder = order;
        log.info(staticOrder.toString());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
        sendMessage.setText("Введите работы по данному заказу ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("add_order_step_two");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }

    public SendMessage addOrderTwoStep(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        if (query != null && query.equals("/cancelWithOrder")) {
            return toGeneralWithOrder(update);
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        String orderWorks;
        try {
            orderWorks = update.getMessage().getText();
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
            sendMessage.setText("Проверьте вводимые работы и попробуйте еще раз ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        OrderEntity order = this.staticOrder;
        order.setWorks(orderWorks);
        this.staticOrder = order;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(cancelWithOrderKeyboardPlusWithoutNumber());
        sendMessage.setText("Введите номер телефона в формате 79203658495 ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("add_order_step_three");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }

    public SendMessage addOrderThreeStep(Update update) {
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        if (query != null && query.equals("/cancelWithOrder")) {
            return toGeneralWithOrder(update);
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        String phone;
        if (query != null && query.equals("/without_number")){
            orderService.saveOrder(this.staticOrder);
            this.staticOrder = new OrderEntity();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен. без номера телефона ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        }
        try {
            phone = update.getMessage().getText();
        } catch (PhoneFormatException e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
            sendMessage.setText("Введен неверный номер телефона. Введите номер в формате 79208746587 ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelWithOrderKeyboard());
            sendMessage.setText("Произошла ошибка. Введите номер в формате 79208746587 ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByPhone(phone);
        if (appUserEntity.isPresent()) {
            this.staticOrder.setAppUserEntity(appUserEntity.get());
            orderService.saveOrder(this.staticOrder);
            this.staticOrder = new OrderEntity();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        } else {
            AppUserEntity appUser = new AppUserEntity();
            appUser.setPhone(phone);
            userService.updateUser(appUser);
            this.staticOrder.setAppUserEntity(appUser);
            orderService.saveOrder(this.staticOrder);
            this.staticOrder = new OrderEntity();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(getGeneralKeyboard());
            sendMessage.setText("Заказ успешно добавлен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general");
            adminService.updateAdmin(adminTemp);
            return sendMessage;

        }
    }

    public SendMessage changeOrderStatus(Update update) {
        log.info(update.getMessage().getText());
        String query = null;
        Long chatID = null;
        if (update.hasCallbackQuery()) {
            query = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMessage()) {
            chatID = update.getMessage().getChatId();
        }
        Integer orderNumber;
        try {
            orderNumber = Integer.valueOf(update.getMessage().getText());
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelKeyboard());
            sendMessage.setText("Неверный номер заказа. Номер должен содержать только цифры. Введите другой номер заказа ");
            sendMessage.setChatId(chatID);
            return sendMessage;
        }
        Optional<OrderEntity> order = orderService.findOrderByNumber(orderNumber);
        if (!order.isPresent()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelKeyboard());
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
            sendMessage.setReplyMarkup(getGeneralKeyboard());
            sendMessage.setText("Статус заказа изменен ");
            sendMessage.setChatId(chatID);
            AdminEntity adminTemp = admin.get();
            adminTemp.setState("general");
            adminService.updateAdmin(adminTemp);
            return sendMessage;
        }
    }

    public SendMessage error(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(getGeneralKeyboard());
        sendMessage.setText("Произошла ошибка. Выберете действие: ");
        sendMessage.setChatId(chatId);
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatId);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general");
        adminService.updateAdmin(adminTemp);
        return sendMessage;
    }

    public SendMessage toGeneral(Update update) {
        Long chatID;
        if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatID = update.getMessage().getChatId();
        }
        Optional<AdminEntity> admin = adminService.getAdminByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(getGeneralKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general");
        adminService.updateAdmin(adminTemp);
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
        sendMessage.setReplyMarkup(getGeneralKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AdminEntity adminTemp = admin.get();
        adminTemp.setState("general");
        adminService.updateAdmin(adminTemp);
        this.staticOrder = new OrderEntity();
        return sendMessage;
    }

    private ReplyKeyboard cancelKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
        inlineKeyboardButton1Line1.setText("На главную");
        inlineKeyboardButton1Line1.setCallbackData("/cancel");
        buttonLine1List.add(inlineKeyboardButton1Line1);
        allButton.add(buttonLine1List);
        inlineKeyboardMarkup.setKeyboard(allButton);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboard cancelWithOrderKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
        inlineKeyboardButton1Line1.setText("На главную");
        inlineKeyboardButton1Line1.setCallbackData("/cancelWithOrder");
        buttonLine1List.add(inlineKeyboardButton1Line1);
        allButton.add(buttonLine1List);
        inlineKeyboardMarkup.setKeyboard(allButton);
        return inlineKeyboardMarkup;
    }
    private ReplyKeyboard cancelWithOrderKeyboardPlusWithoutNumber() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
        inlineKeyboardButton1Line1.setText("На главную");
        inlineKeyboardButton1Line1.setCallbackData("/cancelWithOrder");
        buttonLine1List.add(inlineKeyboardButton1Line1);

        List<InlineKeyboardButton> buttonLine2List = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton2Line2 = new InlineKeyboardButton();
        inlineKeyboardButton2Line2.setText("Номер отсутствует");
        inlineKeyboardButton2Line2.setCallbackData("/without_number");
        buttonLine1List.add(inlineKeyboardButton2Line2);
        allButton.add(buttonLine1List);
        allButton.add(buttonLine2List);
        inlineKeyboardMarkup.setKeyboard(allButton);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getGeneralKeyboard() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine2List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine3List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine4List = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
        inlineKeyboardButton1Line1.setText("АКТИВНЫЕ ЗАКАЗЫ");
        inlineKeyboardButton1Line1.setCallbackData("/active_order");
        InlineKeyboardButton inlineKeyboardButton2Line2 = new InlineKeyboardButton();
        inlineKeyboardButton2Line2.setText("ДОБАВИТЬ ЗАКАЗ");
        inlineKeyboardButton2Line2.setCallbackData("/add_order");
        InlineKeyboardButton inlineKeyboardButton3Line3 = new InlineKeyboardButton();
        inlineKeyboardButton3Line3.setText("ИЗМЕНИТЬ СТАТУС ЗАКАЗА");
        inlineKeyboardButton3Line3.setCallbackData("/change_order_status");
        InlineKeyboardButton inlineKeyboardButton4Line4 = new InlineKeyboardButton();
        inlineKeyboardButton4Line4.setText("ОТПРАВИТЬ СООБЩЕНИЕ ВСЕМ КЛИЕНТАМ");
        inlineKeyboardButton4Line4.setCallbackData("/рассылка");

        buttonLine1List.add(inlineKeyboardButton1Line1);
        buttonLine2List.add(inlineKeyboardButton2Line2);
        buttonLine3List.add(inlineKeyboardButton3Line3);
        buttonLine4List.add(inlineKeyboardButton4Line4);

        allButton.add(buttonLine1List);
        allButton.add(buttonLine2List);
        allButton.add(buttonLine3List);
        allButton.add(buttonLine4List);
        inlineKeyboardMarkup.setKeyboard(allButton);
        return inlineKeyboardMarkup;
    }

    public OrderEntity getOrderEntity() {
        return staticOrder;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.staticOrder = orderEntity;
    }
}
