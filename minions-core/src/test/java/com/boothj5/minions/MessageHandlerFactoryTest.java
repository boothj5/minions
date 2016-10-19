package com.boothj5.minions;

import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class MessageHandlerFactoryTest {

    private static final String PREFIX = "!";
    private static final String MY_NICK = "minions";

    private static final String FROM_MINIONS_FULL = "coven@chat.shakespeare.lit/" + MY_NICK;
    private static final String FROM_FULL = "coven@chat.shakespeare.lit/thirdwitch";
    private static final String FROM_BARE = "coven@chat.shakespeare.lit";
    private static final String MESSAGE =
        "<message " +
                "from='" + FROM_FULL + "' " +
            "id='10101' " +
            "to='wiccarocks@shakespeare.lit/laptop' " +
            "type='groupchat'>" +
                "<body>mocked</body>" +
        "</message>";

    private static final String DELAYED_MESSAGE =
        "<message " +
            "from='" + FROM_FULL + "' " +
            "id='10101' " +
            "to='wiccarocks@shakespeare.lit/laptop' " +
            "type='groupchat'>" +
                "<body>mocked</body>" +
                "<delay xmlns='urn:xmpp:delay' " +
                    "from='" + FROM_BARE + "' " +
                    "stamp='2002-09-10T23:05:37Z'/>" +
        "</message>";

    @Test
    public void returnsNopMessageHandler() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, null, null, null);
        Message stanza = new Message();

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsBotCommandHandlerForJars() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "jars");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof BotCommandHandler);
    }

    @Test
    public void returnsBotCommandHandlerForHelp() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "help");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof BotCommandHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenDelayedJars() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(DELAYED_MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "help");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenDelayedHelp() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(DELAYED_MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "help");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenJarsFromMinions() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(DELAYED_MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_MINIONS_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "help");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }

    @Test
    public void returnsNopMessageHandlerWhenHelpFromMinions() {
        MessageHandlerFactory factory = new MessageHandlerFactory(null, PREFIX, null, MY_NICK);
        Message stanza = mock(Message.class);
        given(stanza.toXML()).willReturn(DELAYED_MESSAGE);
        given(stanza.getFrom()).willReturn(FROM_MINIONS_FULL);
        given(stanza.getBody()).willReturn(PREFIX + "help");

        MessageHandler handler = factory.create(stanza);

        assertTrue(handler instanceof NopMessageHandler);
    }
}