package com.boothj5.minions;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MinionsConfigurationTest {

    @Test
    public void throwsExceptionWhenNoUser() {
        Map<String, Object> config = new HashMap<>();

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoUserName() {
        Map<String, Object> config = new HashMap<>();
        config.put("user", new HashMap<String, String>());

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: user.name", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoUserPassword() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username@chatservice");
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
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: rooms", me.getMessage());
        }
    }

    @Test
    public void returnsUserName() {
        Map<String, Object> config = new HashMap<>();
        addValidRooms(config);
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username@chatservice");
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getUser();

        assertEquals("username", actual);
    }

    @Test
    public void returnsUserService() {
        Map<String, Object> config = new HashMap<>();
        addValidRooms(config);
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username@chatservice");
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getService();

        assertEquals("chatservice", actual);
    }

    @Test
    public void returnsUserPassword() {
        Map<String, Object> config = new HashMap<>();
        addValidRooms(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "userpassword";
        user.put("name", "username@chatservice");
        user.put("password", expected);
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPassword();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsUserResource() {
        Map<String, Object> config = new HashMap<>();
        addValidRooms(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "userresource";
        user.put("name", "username@chatservice");
        user.put("password", "userpassword");
        user.put("resource", expected);
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultUserResource() {
        Map<String, Object> config = new HashMap<>();
        addValidRooms(config);
        Map<String, Object> user = new HashMap<>();
        String expected = "minions-core";
        user.put("name", "username@chatservice");
        user.put("password", "userpassword");
        config.put("user", user);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getResource();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsServiceServer() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> service = new HashMap<>();
        String expected = "serviceserver";
        service.put("server", expected);
        config.put("service", service);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getServer();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsNullServiceServer() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        config.put("service", new HashMap<String, Object>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getServer();

        assertNull(actual);
    }

    @Test
    public void returnsServicePort() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> service = new HashMap<>();
        int expected = 1234;
        service.put("port", expected);
        config.put("service", service);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultServicePortWhenNotSpecified() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        config.put("service", new HashMap<>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(5222, actual);
    }

    @Test
    public void returnsDefaultServicePortWhenNoService() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getPort();

        assertEquals(5222, actual);
    }

    @Test
    public void throwsExceptionWhenNoRooms() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: rooms", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoEmptyRooms() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        config.put("rooms", new ArrayList<Map<String, String>>());

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Must have at least one room configured.", me.getMessage());
        }
    }

    @Test
    public void throwsExceptionWhenNoRoomJid() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        Map<String, String> room1 = new HashMap<>();
        room1.put("nick", "minions");
        List<Map<String, String>> rooms = new ArrayList<>();
        rooms.add(room1);
        config.put("rooms", rooms);

        try {
            new MinionsConfiguration(config);
            fail("Expected MinionsException not thrown");
        } catch (MinionsException me) {
            assertEquals("Missing configuration property: room.jid", me.getMessage());
        }
    }

    @Test
    public void returnsRooms() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        Map<String, String> room1 = new HashMap<>();
        room1.put("jid", "room1@conference.server.org");
        room1.put("nick", "blah");
        Map<String, String> room2 = new HashMap<>();
        room2.put("jid", "room2@conference.server.org");
        room2.put("nick", "bot");
        room2.put("password", "p455w0rd");
        Map<String, String> room3 = new HashMap<>();
        room3.put("jid", "room3@conference.server.org");
        List<Map<String, String>> rooms = Arrays.asList(room1, room2, room3);

        config.put("rooms", rooms);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        List<MinionsRoomConfiguration> roomsConfig = minionsConfiguration.getRooms();

        assertEquals(3, rooms.size());

        MinionsRoomConfiguration config1 = new MinionsRoomConfiguration("room1@conference.server.org", "blah", null);
        assertTrue(roomsConfig.contains(config1));

        MinionsRoomConfiguration config2 = new MinionsRoomConfiguration("room2@conference.server.org", "bot", "p455w0rd");
        assertTrue(roomsConfig.contains(config2));

        MinionsRoomConfiguration config3 = new MinionsRoomConfiguration("room3@conference.server.org", "minions", null);
        assertTrue(roomsConfig.contains(config3));
    }

    @Test
    public void returnsPluginsDir() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = "pluginsdir";
        plugins.put("dir", "pluginsdir");
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsDirWhenNotSpecified() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        String expected = System.getProperty("user.home") + "/.local/share/minions/plugins";
        config.put("plugins", new HashMap<String, Object>());

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsDirWhenNoPlugins() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        String expected = System.getProperty("user.home") + "/.local/share/minions/plugins";

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPluginsDir();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsPluginsRefreshSeconds() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> plugins = new HashMap<>();
        int expected = 60;
        plugins.put("refreshSeconds", expected);
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsRefreshSecondsWhenNotSpecified() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> plugins = new HashMap<>();
        int expected = 10;
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsRefreshSecondsWhenNoPlugins() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        int expected = 10;

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        int actual = minionsConfiguration.getRefreshSeconds();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsPluginsPrefix() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = ":";
        plugins.put("prefix", expected);
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsPrefixWhenNotSpecified() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        Map<String, Object> plugins = new HashMap<>();
        String expected = "!";
        config.put("plugins", plugins);

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    @Test
    public void returnsDefaultPluginsPrefixWhenNoPlugins() {
        Map<String, Object> config = new HashMap<>();
        addValidUser(config);
        addValidRooms(config);
        String expected = "!";

        MinionsConfiguration minionsConfiguration = new MinionsConfiguration(config);
        String actual = minionsConfiguration.getPrefix();

        assertEquals(expected, actual);
    }

    private void addValidUser(Map<String, Object> config) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "username@chatservice");
        user.put("password", "userpassword");
        config.put("user", user);
    }

    private void addValidRooms(Map<String, Object> config) {
        List<Map<String, String>> rooms = new ArrayList<>();
        Map<String, String> room = new HashMap<>();
        room.put("jid", "room@server.org");
        rooms.add(room);
        config.put("rooms", rooms);
    }
}