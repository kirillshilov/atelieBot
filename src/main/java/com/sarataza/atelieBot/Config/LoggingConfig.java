package com.sarataza.atelieBot.Config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class LoggingConfig {

    @PostConstruct
    public void setLogLevel() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Глобальный уровень логирования
        context.getLogger("ROOT").setLevel(Level.INFO);

        // Отключаем подробный лог TelegramBots
        context.getLogger("org.telegram").setLevel(Level.WARN);
        context.getLogger("org.telegram.telegrambots").setLevel(Level.WARN);
        context.getLogger("org.telegram.telegrambots.meta").setLevel(Level.WARN);
    }
}

