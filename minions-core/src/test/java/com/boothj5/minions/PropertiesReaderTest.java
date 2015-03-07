package com.boothj5.minions;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PropertiesReaderTest {

    public static final String MINIONS_USER_NAME = "minions.user.name";
    public static final String MINIONS_USER_SERVICE = "minions.user.service";
    public static final String MINIONS_USER_RESOURCE = "minions.user.resource";
    public static final String MINIONS_USER_PASSWORD = "minions.user.password";
    public static final String MINIONS_SERVICE_SERVER = "minions.service.server";
    public static final String MINIONS_SERVICE_PORT = "minions.service.port";
    public static final String MINIONS_ROOM_JID = "minions.room.jid";
    public static final String MINIONS_ROOM_NICK = "minions.room.nick";
    public static final String MINIONS_ROOM_PASSWORD = "minions.room.password";
    public static final String MINIONS_REFRESH_SECONDS = "minions.refresh.seconds";
    public static final String MINIONS_PREFIX = "minions.prefix";
    public static final String MINIONS_PLUGINSDIR = "minions.pluginsdir";

    @Before
    public void setUp() {
        System.clearProperty(MINIONS_USER_NAME);
        System.clearProperty(MINIONS_USER_SERVICE);
        System.clearProperty(MINIONS_USER_RESOURCE);
        System.clearProperty(MINIONS_USER_PASSWORD);
        System.clearProperty(MINIONS_SERVICE_SERVER);
        System.clearProperty(MINIONS_SERVICE_PORT);
        System.clearProperty(MINIONS_ROOM_JID);
        System.clearProperty(MINIONS_ROOM_NICK);
        System.clearProperty(MINIONS_ROOM_PASSWORD);
        System.clearProperty(MINIONS_REFRESH_SECONDS);
        System.clearProperty(MINIONS_PREFIX);
        System.clearProperty(MINIONS_PLUGINSDIR);
    }

    @Test
    public void readsUserName() {
        String expected = "user";
        System.setProperty(MINIONS_USER_NAME, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getUser();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsIllegalStateExceptionWhenNoUserName() {
        PropertiesReader reader = new PropertiesReader();

        reader.getUser();
    }

    @Test
    public void readsUserService() {
        String expected = "service";
        System.setProperty(MINIONS_USER_SERVICE, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getService();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsIllegalStateExceptionWhenNoUserService() {
        PropertiesReader reader = new PropertiesReader();

        reader.getService();
    }

    @Test
    public void readsUserResource() {
        String expected = "resource";
        System.setProperty(MINIONS_USER_RESOURCE, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void usesDefaultUserResource() {
        String expected = "minions-core";
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void readsUserPassword() {
        String expected = "password";
        System.setProperty(MINIONS_USER_PASSWORD, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getPassword();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsIllegalStateExceptionWhenNoUserPassword() {
        PropertiesReader reader = new PropertiesReader();

        reader.getPassword();
    }

    @Test
    public void readsServiceServer() {
        String expected = "server";
        System.setProperty(MINIONS_SERVICE_SERVER, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getServer();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsNullWhenNoServiceServer() {
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getServer();

        assertNull(actual);
    }

    @Test
    public void readsServicePort() {
        int expected = 5432;
        System.setProperty(MINIONS_SERVICE_PORT, String.valueOf(expected));
        PropertiesReader reader = new PropertiesReader();

        int actual = reader.getPort();

        assertEquals(expected, actual);
    }

    @Test
    public void usesDefaultServicePort() {
        PropertiesReader reader = new PropertiesReader();

        int actual = reader.getPort();

        assertEquals(5222, actual);
    }

    @Test(expected = NumberFormatException.class)
    public void throwsNumberFormatExceptionWhenServicePortNotANumber() {
        System.setProperty(MINIONS_SERVICE_PORT, "notanumber");
        PropertiesReader reader = new PropertiesReader();

        reader.getPort();
    }

    @Test
    public void readsRoomJid() {
        String expected = "myroom@myserver.org";
        System.setProperty(MINIONS_ROOM_JID, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getRoom();

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void throwsIllegalStateExceptionWhenNoRoomJid() {
        PropertiesReader reader = new PropertiesReader();

        reader.getRoom();
    }

    @Test
    public void readsRoomNick() {
        String expected = "mynick";
        System.setProperty(MINIONS_ROOM_NICK, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getRoomNick();

        assertEquals(expected, actual);
    }

    @Test
    public void readsRoomPassword() {
        String expected = "roompassword";
        System.setProperty(MINIONS_ROOM_PASSWORD, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getRoomPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsNullWhenNoRoomPassword() {
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getRoomPassword();

        assertNull(actual);
    }

    @Test
    public void usesDefaultRoomNick() {
        String expected = "minions";
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getRoomNick();

        assertEquals(expected, actual);
    }

    @Test
    public void readsRefreshSeconds() {
        int expected = 60;
        System.setProperty(MINIONS_REFRESH_SECONDS, String.valueOf(expected));
        PropertiesReader reader = new PropertiesReader();

        int actual = reader.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void usesDefaultRefreshSeconds() {
        PropertiesReader reader = new PropertiesReader();

        int actual = reader.getRefreshSeconds();

        assertEquals(10, actual);
    }

    @Test(expected = NumberFormatException.class)
    public void throwsNumberFormatExceptionWhenRefreshSecondsNotANumber() {
        System.setProperty(MINIONS_REFRESH_SECONDS, "badnumber");
        PropertiesReader reader = new PropertiesReader();

        reader.getRefreshSeconds();
    }

    @Test
    public void readsPrefix() {
        String expected = ":";
        System.setProperty(MINIONS_PREFIX, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void usesDefaultPrefix() {
        String expected = "!";
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void readsPluginsDir() {
        String expected = "/home/bob/minions";
        System.setProperty(MINIONS_PLUGINSDIR, expected);
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void usesDefaultPluginsDir() {
        String expected = System.getProperty("user.home") + "/.local/share/minions/plugins";
        PropertiesReader reader = new PropertiesReader();

        String actual = reader.getPluginsDir();

        assertEquals(expected, actual);
    }
}