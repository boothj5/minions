package com.boothj5.minions;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MinionsListenerTest {

    @Mock
    MinionsConfiguration config;

    @Mock
    MinionStore minionsStore;

    @Mock
    Message message;

    @Mock
    private MinionsRoom room;

    private MinionsListener listener;

    @Before
    public void setup() {
        reset(config);
        reset(minionsStore);
        reset(room);
        listener = new MinionsListener(config, minionsStore, room);
    }

    @Test
    public void sendsNothingOnNonMessagePackets() {
        listener.processPacket(new Packet() {
            @Override
            public String toXML() {
                return null;
            }
        });

        verifyZeroInteractions(minionsStore);
    }

    @Test
    public void doesNothingOnEmptyBody() {
        given(message.getBody()).willReturn(null);
        given(message.getFrom()).willReturn("room@conference.server.org/someone");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verifyZeroInteractions(minionsStore);
    }

    @Test
    public void doesNothingOnDelayedMessage() {
        given(message.getBody()).willReturn("!hey there");
        given(message.getFrom()).willReturn("room@conference.server.org/someone");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(mock(PacketExtension.class));
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verifyZeroInteractions(minionsStore);
    }

    @Test
    public void doesNothingOnMessageFromRoom() {
        given(message.getBody()).willReturn("Message body");
        given(message.getFrom()).willReturn("room@conference.server.org");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verifyZeroInteractions(minionsStore);
    }

    @Test
    public void doesNothingOnMessageFromSelf() throws XMPPException {
        given(message.getBody()).willReturn("Message body");
        given(message.getFrom()).willReturn("room@conference.server.org/minions");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verifyZeroInteractions(minionsStore);
    }

    @Test
    public void callsOnRoomMessage() {
        String messageBody = "Message body";
        String fromNick = "bobby";
        given(message.getBody()).willReturn(messageBody);
        given(message.getFrom()).willReturn("room@conference.server.org/" + fromNick);
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verify(minionsStore).onMessage(messageBody, fromNick);
    }

    @Test
    public void callsOnHelp() throws XMPPException {
        given(message.getBody()).willReturn("!help");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verify(minionsStore).onHelp();
    }

    @Test
    public void callsOnJars() throws XMPPException {
        given(message.getBody()).willReturn("!jars");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verify(minionsStore).onJars();
    }

    @Test
    public void callsOnCommand() throws XMPPException {
        given(message.getBody()).willReturn("!dosomething with these args");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(message.getExtension("delay", "urn:xmpp:delay")).willReturn(null);
        given(config.getPrefix()).willReturn("!");
        given(room.getNick()).willReturn("minions");

        listener.processPacket(message);

        verify(minionsStore).onCommand("!dosomething with these args", "bobby");
    }
}