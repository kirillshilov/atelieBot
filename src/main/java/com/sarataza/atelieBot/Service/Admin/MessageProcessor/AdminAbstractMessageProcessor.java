package com.sarataza.atelieBot.Service.Admin.MessageProcessor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AdminAbstractMessageProcessor {
    public SendMessage proceed (Update update);
}
