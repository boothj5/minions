package com.boothj5.commandbot.api;

public interface CommandBotRoom {
    void sendMessage(String message) throws CommandBotException;
}
