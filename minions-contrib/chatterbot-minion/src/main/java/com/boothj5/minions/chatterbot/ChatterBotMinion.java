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
    private String botName;

    private ChatterBotSession jabberwackyBotSession;
    private ChatterBotSession cleverBotSession;
    private ChatterBotSession chosenSession;

    public ChatterBotMinion() {
        ChatterBotFactory factory = new ChatterBotFactory();
        ChatterBot cleverBot;
        ChatterBot jabberwackyBot;
        try {
            jabberwackyBot = factory.create(ChatterBotType.JABBERWACKY);
            jabberwackyBotSession = jabberwackyBot.createSession();
        } catch (Exception e) {
            LOG.debug("Error loading jabberwacky");
            e.printStackTrace();
        }
        try {
            cleverBot = factory.create(ChatterBotType.CLEVERBOT);
            cleverBotSession = cleverBot.createSession();
        } catch (Exception e) {
            LOG.debug("Error loading cleverbot");
            e.printStackTrace();
        }

        chosenSession = cleverBotSession != null ? cleverBotSession : jabberwackyBotSession;
        botName = cleverBotSession != null ? "cleverbot" : "jabberwacky";
    }

    @Override
    public String getHelp() {
        return "[message|set bot] - Send a message to chatterbot. Set bot to cleverbot, or jabberwacky";
    }

    @Override
    public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {
        switch (message) {
            case "set cleverbot":
                chosenSession = cleverBotSession;
                botName = "cleverbot";
                LOG.debug("Bot set to cleverbot");
                muc.sendMessage("Bot set to cleverbot");
                break;
            case "set jabberwacky":
                chosenSession = jabberwackyBotSession;
                botName = "jabberwacky";
                LOG.debug("Bot set to jabberwacky");
                muc.sendMessage("Bot set to jabberwacky");
                break;
            default:
                String response;
                try {
                    LOG.debug("Sending to bot: " + message);
                    response = chosenSession.think(message);
                    LOG.debug("Received from bot:" + response);
                } catch (Exception e) {
                    LOG.debug("Error from cleverbot:", e);
                    muc.sendMessage("Error talking to chatterbot: " + botName);
                    return;
                }
                muc.sendMessage(botName + " -> " + from + ": " + response);
                break;
        }
    }
}