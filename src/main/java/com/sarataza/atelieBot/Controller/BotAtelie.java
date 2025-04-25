package com.sarataza.atelieBot.Controller;


import com.sarataza.atelieBot.Model.AppUserEntity;
import com.sarataza.atelieBot.Model.MessageAdminAndUser;
import com.sarataza.atelieBot.Repository.AdminLoginRepository;
import com.sarataza.atelieBot.Config.BotConfig;
import com.sarataza.atelieBot.Model.AdminEntity;
import com.sarataza.atelieBot.Repository.UserRepository;
import com.sarataza.atelieBot.Service.Admin.MessageProcessor.AdminAbstractMessageProcessor;
import com.sarataza.atelieBot.Service.Admin.UtilityAdminService;
import com.sarataza.atelieBot.Service.User.MessageProcessor.UserAbstractMessageProcessor;
import com.sarataza.atelieBot.Service.User.UtilityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class BotAtelie extends TelegramLongPollingBot {
    private final Map<String, UserAbstractMessageProcessor> userMessageProcessor;
    private final Map<String, AdminAbstractMessageProcessor> adminMessageProcessor;
    private final BotConfig botConfig;
    private final AdminLoginRepository adminLoginRepository;
    private final UserRepository userRepository;
    private final UtilityUserService utilityUserService;
    private final UtilityAdminService utilityAdminService;


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
        Long chatId = null;
        if (update.hasMessage() && update.getMessage() != null) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage() != null) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            log.warn("Received update without message or callback query: {}", update);
        }
        log.warn(chatId.toString());
        if (chatId == null) {
            log.error("Chat ID could not be determined for update: {}", update);
            return;
        }
        SendMessage sendMessage = new SendMessage();
        Optional<AdminEntity> admin = adminLoginRepository.getAdminEntityByLogin(chatId);
        Optional<AppUserEntity> user = userRepository.getAppUserEntitiesByLogin(chatId);
        if (admin.isPresent()) {
            try {
                if (admin.get().getState() == null) {
                    admin.get().setState("general");
                }
                if (admin.get().getState().contains("initMailing") ){
                    sendMessage = sendMessageToUserMailing(update);
                }
                else{
                    AdminAbstractMessageProcessor processor = adminMessageProcessor.get(admin.get().getState());
                    if (processor != null) {
                        sendMessage = adminMessageProcessor.get(admin.get().getState()).proceed(update);
                    }else
                        sendMessage = utilityAdminService.toGeneral(update);
                    }
            } catch (Exception e) {
                log.warn(e.getMessage());
                sendMessage = utilityAdminService.toGeneral(update);
            }
            sendMessageToUser(sendMessage);
        } else if (user.isPresent()) {
            try {
                if (user.get().getState() == null) {
                    user.get().setState("general");
                }
                UserAbstractMessageProcessor userAbstractMessageProcessor = userMessageProcessor.get(user.get().getState());
                if (userAbstractMessageProcessor != null) {
                    sendMessage = userAbstractMessageProcessor.proceed(update);
                } else {
                    sendMessage = userMessageProcessor.get("error").proceed(update);
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
                sendMessage = utilityUserService.toGeneral(update);
            }
            sendMessageToUser(sendMessage);
        } else {
            try {
                sendMessage = userMessageProcessor.get("registration").proceed(update);
                sendMessageToUser(sendMessage);
                log.warn(sendMessage.toString());
            } catch (Exception e) {
                log.warn(e.getMessage());
                sendMessage = utilityUserService.toGeneral(update);
                sendMessageToUser(sendMessage);
            }
        }

    }

    public void sendMessageToUser(SendMessage sendMessage) {
        try {
            log.warn(sendMessage.toString());
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn(e.toString());
        }
    }

    public SendMessage sendMessageToUserMailing(Update update) {
        MessageAdminAndUser messageAdminAndUser = utilityAdminService.doMailing(update);
        if (messageAdminAndUser.getUserList() == null || messageAdminAndUser.getUserMessage() == null) {
            return messageAdminAndUser.getAdminMessage();
        }
        for (AppUserEntity user : messageAdminAndUser.getUserList()) {
            if (user.getLogin() != null) {
                SendMessage sendMessage = messageAdminAndUser.getUserMessage();
                sendMessage.setChatId(user.getLogin());
                sendMessageToUser(sendMessage);
            }
        }
        messageAdminAndUser.getAdminMessage().setText("Рассылка прошла успешно");
        return messageAdminAndUser.getAdminMessage();
    }
}