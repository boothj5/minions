/*
 * Copyright 2015 - 2016 James Booth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boothj5.minions;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class MinionsRoomImpl implements MinionsRoom {
    private final MultiUserChat muc;

    MinionsRoomImpl(MultiUserChat muc) {
        this.muc = muc;
    }

    @Override
    public String getNick() {
        return muc.getNickname();
    }

    @Override
    public List<String> getOccupants() {
        List<String> fullJids = new ArrayList<>();
        muc.getOccupants().forEachRemaining(fullJids::add);

        List<String> nicks = fullJids.stream()
            .map(JabberID::new)
            .filter(jid -> jid.getResource().isPresent())
            .filter(jid -> !jid.getResource().get().equals(this.getNick()))
            .map(jid -> jid.getResource().get())
            .collect(Collectors.toList());

        return nicks;
    }

    @Override
    public Optional<String> getOccupantPresence(String nick) {
        Presence presence = muc.getOccupantPresence(this.getRoom() + "/" + nick);
        if (presence == null) {
            return Optional.empty();
        }

        if (presence.getMode() == null || presence.getMode().equals(Presence.Mode.available)) {
            return Optional.of("online");
        }

        return Optional.of(presence.getMode().toString());
    }

    @Override
    public String getRoom() {
        return muc.getRoom();
    }

    @Override
    public void sendMessage(String message) {
        try {
            muc.sendMessage(message);
        } catch (XMPPException e) {
            throw new MinionsException(e);
        }
    }
}
