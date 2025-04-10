package com.sarataza.atelieBot.Controller;


import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.MessageAdminAndUser;
import com.sarataza.atelieBot.Repository.AdminLoginRepository;
import com.sarataza.atelieBot.Config.BotConfig;
import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Service.AdminBotService;
import com.sarataza.atelieBot.Service.UserBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class BotAtelie extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final AdminLoginRepository adminLoginRepository;
    private final UserRepository userRepository;
    private final UserBotService userBotService;
    private final AdminBotService adminBotService;


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
        Long chat;
        if (update.hasMessage() && update.getMessage().getChatId() != null) {
            chat = update.getMessage().getChatId();
        } else {
            chat = update.getCallbackQuery().getMessage().getChatId();
        }
        SendMessage sendMessage = new SendMessage();
        Optional<AdminEntity> admin = adminLoginRepository.getAdminEntityByLogin(chat);
        Optional<AppUserEntity> user = userRepository.getAppUserEntitiesByLogin(chat);
        if (admin.isPresent()) {
            try {
                if (admin.get().getState() == null) {
                    admin.get().setState("general");
                }
                switch (admin.get().getState()) {
                    case ("general") -> {
                        sendMessage = adminBotService.general(update);
                    }
                    case ("change_order_status_ready") -> {
                        sendMessage = adminBotService.changeOrderStatus(update);
                    }
                    case ("add_order_begin") -> {
                        sendMessage = adminBotService.addOrderFirstStep(update);
                    }
                    case ("add_order_step_two") -> {
                        sendMessage = adminBotService.addOrderTwoStep(update);
                    }
                    case ("add_order_step_three") -> {
                        sendMessage = adminBotService.addOrderThreeStep(update);
                    }
                    case ("initMailing") -> {
                        sendMessage = sendMessageToUserMailing(update);
                    }

                }
            } catch (Exception e){
                    log.warn(e.getMessage());
                    sendMessage = adminBotService.toGeneral(update);
                }
                sendMessageToUser(sendMessage);
        } else if (user.isPresent()) {
            try {
                if (user.get().getState() == null) {
                    user.get().setState("general");
                }
                switch (user.get().getState()) {
                    case ("general") -> {
                        sendMessage = userBotService.general(update);
                    }
                    case ("add_order") -> {
                        sendMessage = userBotService.addOrder(update);
                    }
                    case ("send_contact") -> {
                        sendMessage = userBotService.addContact(update);
                    }
                    default -> {
                        sendMessage = userBotService.error(update);
                    }
                }
            }catch (Exception e){
                log.warn(e.getMessage());
                sendMessage = userBotService.toGeneral(update);
            }
                sendMessageToUser(sendMessage);

            } else {
            try {
            sendMessage = userBotService.registrationUser(update);
            sendMessageToUser(sendMessage);
            }catch (Exception e){
                log.warn(e.getMessage());
                sendMessage = userBotService.toGeneral(update);
                sendMessageToUser(sendMessage);
            }
        }

    }

    public void sendMessageToUser(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn(e.toString());
        }
    }
    public SendMessage sendMessageToUserMailing(Update update) {
        MessageAdminAndUser messageAdminAndUser = adminBotService.doMailing(update);
        if (messageAdminAndUser.getUserList() == null || messageAdminAndUser.getUserMessage() == null){
            return messageAdminAndUser.getAdminMessage();
        }
        for (AppUserEntity user: messageAdminAndUser.getUserList() ) {
            if(user.getLogin() != null){
            SendMessage sendMessage = messageAdminAndUser.getUserMessage();
            sendMessage.setChatId(user.getLogin());
            sendMessageToUser(sendMessage);}
        }
        messageAdminAndUser.getAdminMessage().setText("Рассылка прошла успешно");
        return messageAdminAndUser.getAdminMessage();
    }
}