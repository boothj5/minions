package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BotCommandHandler extends MessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BotCommandHandler.class);

    BotCommandHandler(Message stanza, MinionStore minions, String minionsPrefix, MinionsRoom muc) {
        super(stanza, minions, minionsPrefix, muc);
    }

    @Override
    public void execute() {
        String command = stanza.getBody().substring(minionsPrefix.length());
        try {
            switch (command) {
                case "help":
                    LOG.debug("Handling help.");
                    handleHelp();
                    break;
                case "jars":
                    LOG.debug("Handling jars.");
                    handleJars();
                    break;
                default:
                    muc.sendMessage("Unknown command: " + command);
                    break;
            }
        } catch (MinionsException e) {
            e.printStackTrace();
        }
    }

    private void handleJars() {
        try {
            minions.lock();
            List<MinionJar> jars = minions.getJars();
            StringBuilder builder = new StringBuilder();
            for (MinionJar jar : jars) {
                builder.append("\n");
                builder.append(jar.getName());
                builder.append(", last updated: ");
                Date date = new Date(jar.getTimestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                String format = simpleDateFormat.format(date);
                builder.append(format);
            }
            muc.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException e) {
            e.printStackTrace();
        }

    }

    private void handleHelp() {
        try {
            minions.lock();
            List<String> commands = minions.commandList();
            StringBuilder builder = new StringBuilder();
            for (String command : commands) {
                builder.append("\n");
                builder.append(minionsPrefix);
                builder.append(command);
                builder.append(" ");
                builder.append(minions.get(command).getHelp());
            }
            muc.sendMessage(builder.toString());
            minions.unlock();
        } catch (InterruptedException ie) {
            LOG.error("Interrupted waiting for minions lock", ie);
        } catch (MinionsException me) {
            LOG.error("Error sending message to room", me);
        }
    }
}