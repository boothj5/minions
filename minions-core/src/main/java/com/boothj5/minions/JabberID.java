package com.boothj5.minions;

import java.util.Optional;

public class JabberID {

    private final String jid;

    JabberID(String jid) {
        this.jid = jid;
    }

    public String getJid() {
        return jid;
    }

    public Optional<String> getResource() {
        if (jid == null) {
            return Optional.empty();
        }

        if (jid.indexOf('/') == -1) {
            return Optional.empty();
        }

        String[] splitJid = jid.split("/");
        if (splitJid.length < 2) {
            return Optional.empty();
        } else {
            return Optional.of(splitJid[1]);
        }
    }
}
