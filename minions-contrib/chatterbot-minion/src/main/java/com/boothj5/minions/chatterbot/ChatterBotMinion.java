package com.boothj5.minions.chatterbot;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class ChatterBotMinion implements Minion {
    public static final String COMMAND = "chatter";
    private final ChatterBotFactory factory;
    private ChatterBotSession botsession;

    public ChatterBotMinion() {
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
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            muc.sendMessage(botsession.think(message.substring(8)));
        } catch (Exception e) {
            throw new MinionsException("Error sending message: " + message);
        }
    }
}
