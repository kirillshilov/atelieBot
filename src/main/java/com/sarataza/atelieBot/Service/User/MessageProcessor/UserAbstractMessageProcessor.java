package com.sarataza.atelieBot.Service.User.MessageProcessor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserAbstractMessageProcessor {
    public SendMessage proceed (Update update);
}
