package com.boothj5.minions;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

import java.util.Map;

class AdminChatManagerListener implements ChatManagerListener {

    private final MinionsConfiguration config;
    private final Map<String, MinionsRoom> rooms;

    AdminChatManagerListener(MinionsConfiguration config, Map<String, MinionsRoom> rooms) {
        this.config = config;
        this.rooms = rooms;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally) {
            chat.addMessageListener(new AdminMessageListener(config, rooms));
        }
    }
}
