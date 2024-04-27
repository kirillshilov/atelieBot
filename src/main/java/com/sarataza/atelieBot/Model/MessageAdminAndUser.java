package com.sarataza.atelieBot.Model;

import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Data
public class MessageAdminAndUser {
private final SendMessage adminMessage;
private final SendMessage userMessage;
private final List<AppUserEntity> userList;
}
