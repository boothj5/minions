package com.boothj5.commandbot.plugins.chatterbot;

import com.boothj5.commandbot.api.CommandBotException;
import com.boothj5.commandbot.api.CommandBotPlugin;
import com.boothj5.commandbot.api.CommandBotRoom;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class ChatterBotPlugin implements CommandBotPlugin {
    public static final String COMMAND = "chatter";
    private final ChatterBotFactory factory;
    private ChatterBotSession botsession;

    public ChatterBotPlugin() {
        factory = new ChatterBotFactory();
        ChatterBot bot = null;
        try {
            bot = factory.create(ChatterBotType.JABBERWACKY);
            botsession = bot.createSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " [message] - Send a message to chatterbot.";
    }

    @Override
    public void onMessage(CommandBotRoom muc, String from, String message) throws CommandBotException {
        try {
            muc.sendMessage(botsession.think(message.substring(8)));
        } catch (Exception e) {
            throw new CommandBotException("Error sending message: " + message);
        }
    }
}
