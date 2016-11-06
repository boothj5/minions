package com.boothj5.minions;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminMessageListenerTest {

    @Mock
    Message message;

    @Mock
    Chat chat;

    private AdminMessageListener listener;

    @Test
    public void exitsOnEmptyMessage() {
        listener = new AdminMessageListener(null, null);

        listener.processMessage(chat, message);

        verifyZeroInteractions(chat);
    }

    @Test
    public void exitsOnNoBarejid() {
        listener = new AdminMessageListener(null, null);

        given(message.getBody()).willReturn("message");
        given(message.getFrom()).willReturn("");

        listener.processMessage(chat, message);

        verifyZeroInteractions(chat);
    }

    @Test
    public void exitsOnNonAdminUser() {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.com", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);
        listener = new AdminMessageListener(config, null);

        given(message.getBody()).willReturn("message");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verifyZeroInteractions(chat);
    }

    @Test
    public void respondsWithHelp() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);
        listener = new AdminMessageListener(config, null);

        given(message.getBody()).willReturn("help");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("\n" +
            "help - This help\n" +
            "rooms - List rooms I'm currently in\n" +
            "occupants <room> - List occupants in room\n" +
            "send <room> <message> - Send a message to the specified room\n" +
            "me <room> <message> - Send a /me message to the specified room");
    }

    @Test
    public void respondsWithErrorWhenInvalidHelpCommand() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);
        listener = new AdminMessageListener(config, null);

        given(message.getBody()).willReturn("help please");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Invalid command usage... duh");
    }

    @Test
    public void respondsWithRoomList() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);
        MinionsRoom room1 = mock(MinionsRoom.class);
        given(room1.getNick()).willReturn("minions");
        given(room1.getRoom()).willReturn("room1@conference.server.org");
        MinionsRoom room2 = mock(MinionsRoom.class);
        given(room2.getNick()).willReturn("bot");
        given(room2.getRoom()).willReturn("room2@conference.server.org");
        MinionsRoom room3 = mock(MinionsRoom.class);
        given(room3.getNick()).willReturn("minions");
        given(room3.getRoom()).willReturn("room3@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room1@conference.server.org", room1);
        rooms.put("room2@conference.server.org", room2);
        rooms.put("room3@conference.server.org", room3);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("rooms");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("\n" +
            "room3@conference.server.org as minions\n" +
            "room2@conference.server.org as bot\n" +
            "room1@conference.server.org as minions");
    }

    @Test
    public void respondsWithErrorWhenInvalidRoomsCommand() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);
        listener = new AdminMessageListener(config, null);

        given(message.getBody()).willReturn("rooms here");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Invalid command usage... duh");
    }

    @Test
    public void sendsMessageToRoom() {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("send room@conference.server.org hello world!");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(room).sendMessage("hello world!");
    }

    @Test
    public void messageRespondsWithErrorWhenRoomNotFound() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("send wrongroom@conference.server.org hello world!");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Room doesn't exist :/");
    }

    @Test
    public void messageRespondsWithErrorWhenInvalidArgNumber() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("send oops");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Invalid command usage... duh");
    }

    @Test
    public void sendsMeMessageToRoom() {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("me room@conference.server.org is running");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(room).sendMessage("/me is running");
    }

    @Test
    public void meMessageRespondsWithErrorWhenRoomNotFound() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("me wrongroom@conference.server.org is running");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Room doesn't exist :/");
    }

    @Test
    public void meMessageRespondsWithErrorWhenInvalidArgNumber() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("me arg");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Invalid command usage... duh");
    }

    @Test
    public void respondsWithErrorWhenUnknownCommand() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("wtf");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("I didn't understand that...");
    }

    @Test
    public void respondsWithOccupants() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        List<String> occupants = Arrays.asList("mike", "sarah", "dave", "alice");
        given(room.getOccupants()).willReturn(occupants);

        given(room.getOccupantPresence("mike")).willReturn(Optional.of("online"));
        given(room.getOccupantPresence("sarah")).willReturn(Optional.of("chat"));
        given(room.getOccupantPresence("dave")).willReturn(Optional.empty());
        given(room.getOccupantPresence("alice")).willReturn(Optional.of("dnd"));
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("occupants room@conference.server.org");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("\nroom@conference.server.org occupants:\nmike (online)\nsarah (chat)\ndave\nalice (dnd)");
    }

    @Test
    public void respondsWithErrorWhenRoomDoesntExist() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        List<String> occupants = Arrays.asList("mike", "sarah", "dave", "alice");
        given(room.getOccupants()).willReturn(occupants);
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("occupants bad@conference.server.org");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Room doesn't exist :/");
    }

    @Test
    public void respondsWithErrorWhenInvalidArgNum() throws XMPPException {
        MinionsConfiguration config = mock(MinionsConfiguration.class);
        List<String> admins = Arrays.asList("someone@server.org", "eddie@maiden.org", "bobster@jabber.org");
        given(config.getAdmins()).willReturn(admins);

        MinionsRoom room = mock(MinionsRoom.class);
        given(room.getNick()).willReturn("bot");
        given(room.getRoom()).willReturn("room@conference.server.org");
        List<String> occupants = Arrays.asList("mike", "sarah", "dave", "alice");
        given(room.getOccupants()).willReturn(occupants);
        Map<String, MinionsRoom> rooms = new HashMap<>();
        rooms.put("room@conference.server.org", room);
        listener = new AdminMessageListener(config, rooms);

        given(message.getBody()).willReturn("occupants asdf asdfasdf asdf");
        given(message.getFrom()).willReturn("eddie@maiden.org/laptop");

        listener.processMessage(chat, message);

        verify(chat).sendMessage("Invalid command usage... duh");
    }
}
