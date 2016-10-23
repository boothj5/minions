package com.boothj5.minions;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.fail;

public class MinionsRoomConfigurationTest {

    @Test
    public void throwsExceptionWhenNoJid() {
        try {
            new MinionsRoomConfiguration(null, null, null);
            fail("Expected MinionsException not thrown.");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: room.jid", me.getMessage());
        }
    }

    @Test
    public void returnsJid() {
        MinionsRoomConfiguration config = new MinionsRoomConfiguration("jid", null, null);

        assertEquals("jid", config.getJid());
    }

    @Test
    public void returnsNick() {
        MinionsRoomConfiguration config = new MinionsRoomConfiguration("jid", "bob", null);

        assertEquals("bob", config.getNick());
    }

    @Test
    public void defaultsNick() {
        MinionsRoomConfiguration config = new MinionsRoomConfiguration("jid", null, null);

        assertEquals("minions", config.getNick());
    }

    @Test
    public void returnsNoPassword() {
        MinionsRoomConfiguration config = new MinionsRoomConfiguration("jid", null, null);

        assertFalse(config.getPassword().isPresent());
    }

    @Test
    public void returnsPassword() {
        MinionsRoomConfiguration config = new MinionsRoomConfiguration("jid", null, "pass");

        assertEquals("pass", config.getPassword().get());
    }
}