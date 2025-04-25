package com.sarataza.atelieBot.Service.User;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class UserKeyboardFactory {
    public static InlineKeyboardMarkup getGeneralKeyboard() {

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
    public static ReplyKeyboardMarkup getContactKeyboard() {
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
    public static ReplyKeyboard getCancelKeyboard() {
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
}
