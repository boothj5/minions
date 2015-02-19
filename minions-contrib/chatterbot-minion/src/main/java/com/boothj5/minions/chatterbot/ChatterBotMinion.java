package com.boothj5.minions.chatterbot;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatterBotMinion extends Minion {
    private static final Logger LOG = LoggerFactory.getLogger(ChatterBotMinion.class);

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
    public String getHelp() {
        return "[message] - Send a message to chatterbot.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        String think;
        try {
            LOG.debug("Sending to bot: " + message);
            think = botsession.think(message);
            LOG.debug("Received from bot:" + think);
        } catch (Exception e) {
            muc.sendMessage("Error talking to chatterbot.");
            return;
        }

        muc.sendMessage(think);
    }
}
