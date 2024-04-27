package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Repository.OrderRepository;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Util.CONSTANTS;
import com.sarataza.atelieBot.Util.PhoneService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserBotService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderService orderService;
    private final PhoneService phoneService;

    public SendMessage general(Update update) {
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
            return toGeneral(update);
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
                sendMessage.setReplyMarkup(generalKeyboard());
                sendMessage.setText(text.toString());
                sendMessage.setChatId(chatID);
                return sendMessage;
            }
            case "/faq" -> {
                return faq(update);
            }
            case "/cancel" -> {
                return toGeneral(update);
            }
            case "/add_order" -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(cancelKeyboard());
                sendMessage.setText("Введите номер заказа из квитанции ");
                sendMessage.setChatId(chatID);
                AppUserEntity appUser = appUserEntity.get();
                appUser.setState("add_order");
                userService.updateUser(appUser);
                return sendMessage;
            }
            case "/send_contact" -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyMarkup(cancelKeyboard());
                sendMessage.setText("Для отправки контактных данных нажмите кнопку ОТПРАВИТЬ КОНТАКТ ");
                sendMessage.setChatId(chatID);
                sendMessage.setReplyMarkup(getContactKeyboard());
                AppUserEntity appUser = appUserEntity.get();
                appUser.setState("send_contact");
                userService.updateUser(appUser);
                log.info(appUser.toString());
                return sendMessage;
            }
            default -> toGeneral(update);

        }
        return toGeneral(update);
    }

    public SendMessage addContact(Update update) {
        Long chatId = update.getMessage().getChatId();
        AppUserEntity appUser = new AppUserEntity();
        if(!update.hasMessage() || !update.getMessage().hasContact()){
            return toGeneral(update);
        }
        String phone = update.getMessage().getContact().getPhoneNumber();
        phone = phoneService.formatPhone(phone);
        List <OrderEntity> orderEntities = new ArrayList<>();
        Optional<AppUserEntity> user = userService.getAppUserByPhone(phone);
        appUser = userService.getAppUserByLogin(chatId).get();
        if (user.isPresent() &&
                user.get().getId() != appUser.getId()){
            orderEntities = orderRepository.getOrderEntitiesByAppUserEntityId(user.get().getId());
            for (OrderEntity order: orderEntities) {
                order.setAppUserEntity(appUser);
                log.info(orderService.saveOrder(order).toString());
            }
            userService.deleteUser(user.get().getId());
        }
        StringBuilder text = new StringBuilder();
            appUser.setState("general");
            appUser.setPhone(phone);
            appUser.setLogin(update.getMessage().getChatId());
            appUser.setFirstName(update.getMessage().getContact().getFirstName());
            appUser.setLastName(update.getMessage().getContact().getLastName());
            userService.updateUser(appUser);
            SendMessage sendMessage = toGeneral(update);
            List<OrderEntity> orderList = orderService.getAllOrderByUserId(appUser.getId());
            if (orderList.isEmpty()) {
                text.append("Заказы не найдены. Попробуйте найти заказ по номеру из квитанции ");
            } else {
                text.append(orderService.getOrderByStringForUser(orderList));
            }
            sendMessage.setText(text.toString());
            return sendMessage;
    }

    public ReplyKeyboardMarkup getContactKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("ОТПРАВИТЬ КОНТАКТ");
        keyboardButton.setRequestContact(true);
        keyboardFirstRow.add(keyboardButton);
        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    public SendMessage faq(Update update) {
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generalKeyboard());
        sendMessage.setText(CONSTANTS.FAQ);
        sendMessage.setChatId(chatID);
        return sendMessage;
    }

    public SendMessage addOrder(Update update) {
        if (update.hasCallbackQuery() && Objects.equals(update.getCallbackQuery().getData(), "/cancel")){
            return toGeneral(update);
        }
        Long chatId = update.getMessage().getChatId();
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(cancelKeyboard());
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
            temp.setState("general");
            userService.updateUser(temp);
            sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(generalKeyboard());
            sendMessage.setText("Заказ успешно добавлен. Его можно найти в МОИ ЗАКАЗЫ ");
            sendMessage.setChatId(chatId);
            return sendMessage;
        }
        else {
            sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(cancelKeyboard());
            sendMessage.setText("Заказ не найден. Проверьте номер заказа ");
            sendMessage.setChatId(chatId);
            return sendMessage;
        }
    }

    public SendMessage error(Update update) {
        Long chatID = update.getCallbackQuery().getMessage().getChatId();
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generalKeyboard());
        sendMessage.setText("Произошла ошибка. Выберете действие: ");
        sendMessage.setChatId(chatID);
        AppUserEntity temp = appUserEntity.get();
        temp.setState("general");
        userService.updateUser(temp);
        return sendMessage;
    }

    public SendMessage toGeneral(Update update) {
        Long chatID;
        if (update.hasCallbackQuery()) {
            chatID = update.getCallbackQuery().getMessage().getChatId();
        } else {
            chatID = update.getMessage().getChatId();
        }
        Optional<AppUserEntity> appUserEntity = userService.getAppUserByLogin(chatID);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generalKeyboard());
        sendMessage.setText("Выберите действие: ");
        sendMessage.setChatId(chatID);
        AppUserEntity temp = appUserEntity.get();
        temp.setState("general");
        userService.updateUser(temp);
        return sendMessage;
    }

    public SendMessage registrationUser(Update update) {
        AppUserEntity appUserEntity = new AppUserEntity();
        appUserEntity.setFirstName(update.getMessage().getChat().getFirstName());
        appUserEntity.setLastName(update.getMessage().getChat().getLastName());
        appUserEntity.setLogin(update.getMessage().getChatId());
        appUserEntity.setState("general");
        userRepository.save(appUserEntity);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(generalKeyboard());
        sendMessage.setText("Вы успешно прошли регистрацию. Для поиска заказов нажмите ОТПРАВИТЬ КОНТАКТ или ПОИСК ЗАКАЗА. ");
        sendMessage.setChatId(update.getMessage().getChatId());
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

    public InlineKeyboardMarkup generalKeyboard() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine2List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine3List = new ArrayList<>();
        List<InlineKeyboardButton> buttonLine4List = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
        inlineKeyboardButton1Line1.setText("МОИ ЗАКАЗЫ");
        inlineKeyboardButton1Line1.setCallbackData("/orders");
        InlineKeyboardButton inlineKeyboardButton2Line2 = new InlineKeyboardButton();
        inlineKeyboardButton2Line2.setText("О НАС");
        inlineKeyboardButton2Line2.setCallbackData("/faq");
        InlineKeyboardButton inlineKeyboardButton3Line3 = new InlineKeyboardButton();
        inlineKeyboardButton3Line3.setText("ОТПРАВИТЬ КОНТАКТ (поиск заказов по номеру)");
        inlineKeyboardButton3Line3.setCallbackData("/send_contact");
        InlineKeyboardButton inlineKeyboardButton4Line4 = new InlineKeyboardButton();
        inlineKeyboardButton4Line4.setText("ПОИСК ЗАКАЗА");
        inlineKeyboardButton4Line4.setCallbackData("/add_order");

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
}
