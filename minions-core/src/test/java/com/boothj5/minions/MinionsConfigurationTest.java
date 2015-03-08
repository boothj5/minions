package com.boothj5.minions;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MinionsConfigurationTest {

    @Test
    public void throwsExceptionWhenNoUser() {
        Map<String, Map<String, Object>> config = new HashMap<>();

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoUserName() {
        Map<String, Map<String, Object>> config = new HashMap<>();
        config.put("user", new HashMap<String, Object>());

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user.name", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoUserService() {
        Map<String, Map<String, Object>> config = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username");
        config.put("user", user);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user.service", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoUserPassword() {
        Map<String, Map<String, Object>> config = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username");
        user.put("service", "userservice");
        config.put("user", user);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user.password", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoRoom() {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: room", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoRoomJid() {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        config.put("room", new HashMap<String, Object>());

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: room.jid", me.getMessage());
        }
    }

    @Test
    public void returnsUserName() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidRoom(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "username";
        user.put("name", expected);
        user.put("service", "userservice");
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getUser();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsUserService() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidRoom(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "userservice";
        user.put("name", "username");
        user.put("service", expected);
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getService();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsUserPassword() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidRoom(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "userpassword";
        user.put("name", "username");
        user.put("service", "userservice");
        user.put("password", expected);
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsUserResource() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidRoom(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "userresource";
        user.put("name", "username");
        user.put("service", "userservice");
        user.put("password", "userpassword");
        user.put("resource", expected);
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultUserResource() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidRoom(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "minions-core";
        user.put("name", "username");
        user.put("service", "userservice");
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsServiceServer() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> service = new HashMap<>();
        String expected = "serviceserver";
        service.put("server", expected);
        config.put("service", service);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getServer();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsNullServiceServer() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        config.put("service", new HashMap<String, Object>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getServer();

        assertNull(actual);
    }

    @Test
    public void returnsServicePort() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> service = new HashMap<>();
        int expected = 1234;
        service.put("port", expected);
        config.put("service", service);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultServicePortWhenNotSpecified() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        config.put("service", new HashMap<String, Object>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(5222, actual);
    }

    @Test
    public void returnsDefaultServicePortWhenNoService() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(5222, actual);
    }

    @Test
    public void returnsRoomJid() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        Map<String, Object> room = new HashMap<>();
        String expected = "roomjid";
        room.put("jid", expected);
        config.put("room", room);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getRoom();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsRoomNick() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        Map<String, Object> room = new HashMap<>();
        String expected = "roomnick";
        room.put("jid", "roomjid");
        room.put("nick", expected);
        config.put("room", room);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getRoomNick();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultRoomNick() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        Map<String, Object> room = new HashMap<>();
        room.put("jid", "roomjid");
        config.put("room", room);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getRoomNick();

        assertEquals("minions", actual);
    }

    @Test
    public void returnsRoomPassword() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        Map<String, Object> room = new HashMap<>();
        String expected = "roompassword";
        room.put("jid", "roomjid");
        room.put("password", expected);
        config.put("room", room);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getRoomPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsNullRoomPassword() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        Map<String, Object> room = new HashMap<>();
        room.put("jid", "roomjid");
        config.put("room", room);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getRoomPassword();

        assertNull(actual);
    }

    @Test
    public void returnsPluginsDir() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = "pluginsdir";
        plugins.put("dir", "pluginsdir");
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsDirWhenNotSpecified() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        String expected = System.getProperty("user.home") + "/.local/share/minions/plugins";
        config.put("plugins", new HashMap<String, Object>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsDirWhenNoPlugins() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        String expected = System.getProperty("user.home") + "/.local/share/minions/plugins";

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsPluginsRefreshSeconds() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> plugins = new HashMap<>();
        int expected = 60;
        plugins.put("refreshSeconds", expected);
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsRefreshSecondsWhenNotSpecified() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> plugins = new HashMap<>();
        int expected = 10;
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsRefreshSecondsWhenNoPlugins() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        int expected = 10;

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsPluginsPrefix() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = ":";
        plugins.put("prefix", expected);
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsPrefixWhenNotSpecified() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = "!";
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsPrefixWhenNoPlugins() throws MinionsException {
        Map<String, Map<String, Object>> config = new HashMap<>();
        addValidUser(config);
        addValidRoom(config);
        String expected = "!";

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    private void addValidUser(Map<String, Map<String, Object>> config) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username");
        user.put("service", "userservice");
        user.put("password", "userpassword");
        config.put("user", user);
    }

    private void addValidRoom(Map<String, Map<String, Object>> config) {
        Map<String, Object> room = new HashMap<>();
        room.put("jid", "roomjid");
        config.put("room", room);
    }
}