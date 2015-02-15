package com.boothj5.commandbot;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.String.format;

public class BotListener implements PacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(BotListener.class);

    public static final String HELP_COMMAND = "help";

    private final PluginStore plugins;
    private final CommandBotRoom muc;
    private final String myNick;
    private final String commandPrefix;

    public BotListener(PluginStore plugins, String commandPrefix, MultiUserChat muc, String myNick) {
        this.plugins = plugins;
        this.muc = new CommandBotRoomImpl(muc);
        this.myNick = myNick;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public void processPacket(Packet packet) {
        try {
            if (packet instanceof Message) {
                Message messageStanza = (Message) packet;
                String message = messageStanza.getBody();
                if (validCommand(messageStanza)) {
                    if (message.equals(commandPrefix + HELP_COMMAND)) {
                        handleListCommand();
                    } else {
                        handlePluginCommand(messageStanza);
                    }
                }
            }
        } catch (CommandBotException e) {
            e.printStackTrace();
        }
    }

    private void handlePluginCommand(Message message) throws CommandBotException {
        String command = parseCommand(message.getBody());
        if (plugins.exists(command)) {
            LOG.debug(format("Handling command: %s", command));
            plugins.get(command).onMessage(muc, message.getFrom(), message.getBody());
        } else {
            LOG.debug(format("Plugin does not exist: %s", command));
            muc.sendMessage("No such command: " + command);
        }
    }

    private void handleListCommand() throws CommandBotException {
        List<String> commands = plugins.commandList();
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(commandPrefix);
        builder.append(HELP_COMMAND);
        builder.append(" - Show this help.");
        builder.append("\n");
        for (String command : commands) {
            builder.append(commandPrefix);
            builder.append(plugins.get(command).getHelp());
            builder.append("\n");
        }
        muc.sendMessage(builder.toString());
    }

    private String parseCommand(String message) {
        String[] tokens = StringUtils.split(message, " ");
        return tokens[0].substring(commandPrefix.length());
    }

    private boolean validCommand(Message messageStanza) {
        boolean containsBody = messageStanza.getBody() != null;
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);

        if (containsBody && !delayed && !fromMe) {
            LOG.debug("Received: " + messageStanza.getBody());
        }

        boolean isCommand = messageStanza.getBody().startsWith(commandPrefix);

        return containsBody && !delayed && !fromMe && isCommand;
    }
}
