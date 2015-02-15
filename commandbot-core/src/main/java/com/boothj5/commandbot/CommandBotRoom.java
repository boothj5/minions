package com.boothj5.commandbot;

public interface CommandBotRoom {
    void sendMessage(String message) throws CommandBotException;
}
