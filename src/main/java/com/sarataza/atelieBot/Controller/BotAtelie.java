package com.sarataza.atelieBot.Controller;


import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Repository.AdminLoginRepository;
import com.sarataza.atelieBot.Config.BotConfig;
import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Service.UserBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class BotAtelie extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final AdminLoginRepository adminLoginRepository;
    private final UserRepository userRepository;
    private final UserBotService userBotService;


    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info(update.toString());
        Long chat;
        if (update.hasMessage() && update.getMessage().getChatId() != null) {
            chat = update.getMessage().getChatId();
        }
        else {
            chat = update.getCallbackQuery().getMessage().getChatId();
        }
        log.info(chat.toString());
        SendMessage sendMessage = new SendMessage();
        Optional<AdminEntity> admin = adminLoginRepository.getAdminEntityByLogin(chat);
        Optional<AppUserEntity> user = userRepository.getAppUserEntitiesByLogin(chat);
        if (admin.isPresent()) {
            switch (admin.get().getState()) {
                case "/start" -> sendMessage.setText("привет админ");
            }
            sendMessage.setText("привет админ");
            sendMessage.setChatId(chat);
            sendMessageToUser(sendMessage);
        } else if(user.isPresent()) {
          switch (user.get().getState()){
              case ("general") -> {
                  sendMessage = userBotService.general(update);
              }
              case ("add_order") -> {
                  sendMessage = userBotService.addOrder(update);
              }
              case ("send_contact") ->{
                  sendMessage = userBotService.addContact(update);
              }
              default -> {
                  sendMessage = userBotService.error(update);
              }
          }
          sendMessageToUser(sendMessage);
        }
        else {
            sendMessage = userBotService.registrationUser(update);
            sendMessageToUser(sendMessage);
        }
    }

    private void sendMessageToUser(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.info(e.toString());
        }
    }
}