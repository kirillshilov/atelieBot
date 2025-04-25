package com.sarataza.atelieBot.Service.Admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminKeyboardFactory {
   public InlineKeyboardMarkup getGeneralKeyboard() {

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
        inlineKeyboardButton4Line4.setCallbackData("/mailing");

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
     public ReplyKeyboard cancelWithOrderKeyboardPlusWithoutNumber() {
          InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
          List<List<InlineKeyboardButton>> allButton = new ArrayList<>();
          List<InlineKeyboardButton> buttonLine1List = new ArrayList<>();
          InlineKeyboardButton inlineKeyboardButton1Line1 = new InlineKeyboardButton();
          inlineKeyboardButton1Line1.setText("На главную");
          inlineKeyboardButton1Line1.setCallbackData("/cancelWithOrder");
          buttonLine1List.add(inlineKeyboardButton1Line1);

          List<InlineKeyboardButton> buttonLine2List = new ArrayList<>();
          InlineKeyboardButton inlineKeyboardButton2Line2 = new InlineKeyboardButton();
          inlineKeyboardButton2Line2.setText("Добавить без номера");
          inlineKeyboardButton2Line2.setCallbackData("/without_number");
          buttonLine1List.add(inlineKeyboardButton2Line2);
          allButton.add(buttonLine1List);
          allButton.add(buttonLine2List);
          inlineKeyboardMarkup.setKeyboard(allButton);
          return inlineKeyboardMarkup;
     }
     public ReplyKeyboard cancelWithOrderKeyboard() {
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
     public ReplyKeyboard cancelKeyboard() {
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
