package com.boothj5.minions;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class JabberIDTest {
    @Test
    public void returnsJid() {
        JabberID jid = new JabberID("domain.org");
        assertEquals("domain.org", jid.getJid());

        jid = new JabberID("user@domain.org");
        assertEquals("user@domain.org", jid.getJid());

        jid = new JabberID("user@domain.org/resource");
        assertEquals("user@domain.org/resource", jid.getJid());
    }

    @Test
    public void returnsEmptyWhenNoResource() {
        JabberID jid = new JabberID(null);
        assertFalse(jid.getResource().isPresent());

        jid = new JabberID("domain.org");
        assertFalse(jid.getResource().isPresent());

        jid = new JabberID("user@domain.org");
        assertFalse(jid.getResource().isPresent());
    }

    @Test
    public void returnsResource() {
        JabberID jid = new JabberID("user@domain.org/resource");
        assertEquals("resource", jid.getResource().get());
    }
}