package com.boothj5.minions;

import com.boothj5.minions.api.Minion;
import com.boothj5.minions.api.MinionsException;
import com.boothj5.minions.api.MinionsRoom;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.String.format;

public class MinionsListener implements PacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(MinionsListener.class);

    public static final String HELP_COMMAND = "help";

    private final MinionStore minions;
    private final MinionsRoom muc;
    private final String myNick;
    private final String minionsPrefix;

    public MinionsListener(MinionStore minions, String minionsPrefix, MultiUserChat muc, String myNick) {
        this.minions = minions;
        this.muc = new MinionsRoomImpl(muc);
        this.myNick = myNick;
        this.minionsPrefix = minionsPrefix;
    }

    @Override
    public void processPacket(Packet packet) {
        try {
            if (packet instanceof Message) {
                Message messageStanza = (Message) packet;
                String message = messageStanza.getBody();
                if (validCommand(messageStanza)) {
                    if (message.equals(minionsPrefix + HELP_COMMAND)) {
                        handleListCommand();
                    } else {
                        handleMinionsCommand(messageStanza);
                    }
                }
            }
        } catch (MinionsException me) {
            LOG.error("Error processing packet: ", me);
        }
    }

    private void handleMinionsCommand(Message message) throws MinionsException {
        try {
            String command = parseCommand(message.getBody());

            minions.lock();
            Minion minion = minions.get(command);
            if (minion != null) {
                LOG.debug(format("Handling command: %s", command));
                minion.onMessage(muc, message.getFrom(), message.getBody());
            } else {
                LOG.debug(format("Minion does not exist: %s", command));
                muc.sendMessage("No such minion: " + command);
            }
            minions.unlock();
        } catch (InterruptedException ie) {
            throw new MinionsException("Command handler interrupted.", ie);
        }
    }

    private void handleListCommand() throws MinionsException {
        try {
            minions.lock();
            List<String> commands = minions.commandList();
            StringBuilder builder = new StringBuilder();
            builder.append("\n");
            builder.append(minionsPrefix);
            builder.append(HELP_COMMAND);
            builder.append(" - Show this help.");
            builder.append("\n");
            for (String command : commands) {
                builder.append(minionsPrefix);
                builder.append(minions.get(command).getHelp());
                builder.append("\n");
            }
            muc.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            throw new MinionsException("List command interrupted.", ie);
        }
    }

    private String parseCommand(String message) {
        String[] tokens = StringUtils.split(message, " ");
        return tokens[0].substring(minionsPrefix.length());
    }

    private boolean validCommand(Message messageStanza) {
        boolean containsBody = messageStanza.getBody() != null;
        boolean delayed = messageStanza.toXML().contains("delay");
        boolean fromMe = messageStanza.getFrom().endsWith(myNick);

        if (containsBody && !delayed && !fromMe) {
            LOG.debug("Received: " + messageStanza.getBody());
        }

        boolean isCommand = messageStanza.getBody().startsWith(minionsPrefix);

        return containsBody && !delayed && !fromMe && isCommand;
    }
}
