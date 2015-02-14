package com.boothj5.commandbot.plugins;

import com.boothj5.commandbot.CommandBotPlugin;
import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

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
    public void onMessage(MultiUserChat muc, String from, String message) throws XMPPException {
        try {
            muc.sendMessage(botsession.think(message.substring(8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
