package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MessageHandlerFactoryTest {

    private static final String COMMAND_PREFIX = "!";
    private static final String MINIONS_NICK = "minions";

    private static final String ROOM_JID = "coven@chat.shakespeare.lit";
    private static final String MINIONS_JID = "coven@chat.shakespeare.lit/" + MINIONS_NICK;
    private static final String OCCUPANT_JID = "coven@chat.shakespeare.lit/thirdwitch";
    private static final String OCCUPANT_ENDSWITH_MINIONS_JID = "coven@chat.shakespeare.lit/user_" + MINIONS_NICK;

    @Test
    public void returnsNopMessageHandlerOnNullBody() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, null, null, null);
        Message stanza = new Message();

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerOnDelayedMessage() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, null, null, null);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("body");
        given(stanza.toXML()).willReturn("<stanza...delay.../>");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenFromSelf() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, null, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("body");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(MINIONS_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenMessageFromRoom() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, null, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("body");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(ROOM_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsBotMessageHandlerOnHelp() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, COMMAND_PREFIX, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("!help");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(OCCUPANT_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof BotCommandHandler);
    }

    @Test
    public void returnsBotMessageHandlerOnJars() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, COMMAND_PREFIX, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("!jars");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(OCCUPANT_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof BotCommandHandler);
    }

    @Test
    public void returnsMinionsCommandHandlerOnCommand() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, COMMAND_PREFIX, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("!cmd");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(OCCUPANT_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof MinionCommandHandler);
    }

    @Test
    public void returnsRoomMessageHandler() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, COMMAND_PREFIX, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("Hello world!");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(OCCUPANT_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof RoomMessageHandler);
    }

    @Test
    public void returnsRoomMessageHandlerWnenOccupantJidEndsWithSelf() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, COMMAND_PREFIX, null, MINIONS_NICK);
        Message stanza = mock(Message.class);
        given(stanza.getBody()).willReturn("Hello world!");
        given(stanza.toXML()).willReturn("<stanza>");
        given(stanza.getFrom()).willReturn(OCCUPANT_ENDSWITH_MINIONS_JID);

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof RoomMessageHandler);
    }
}