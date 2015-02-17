package com.boothj5.minions.chatterbot;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class ChatterBotMinion extends Minion {
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
        String think;
        try {
            think = botsession.think(message);
        } catch (Exception e) {
            muc.sendMessage("Error talking to chatterbot.");
            return;
        }

        muc.sendMessage(think);
    }
}
