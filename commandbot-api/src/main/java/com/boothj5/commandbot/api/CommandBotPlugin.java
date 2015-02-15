package com.boothj5.commandbot.api;

public interface CommandBotPlugin {
    String getCommand();
    String getHelp();
    void onMessage(CommandBotRoom muc, String from, String message) throws CommandBotException;
}
