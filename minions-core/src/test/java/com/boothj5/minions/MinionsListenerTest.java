package com.boothj5.minions;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MinionsListenerTest {

    @Mock
    MultiUserChat muc;

    @Mock
    MinionsConfiguration config;

    @Mock
    MinionStore minions;

    @Mock
    Message message;

    private MinionsRoom room;
    private MinionsListener listener;

    @Before
    public void setup() {
        reset(config);
        reset(minions);
        reset(muc);
        reset(message);
        room = new MinionsRoomImpl(muc);
        listener = new MinionsListener(config, minions, room);
    }

    @Test
    public void sendsNothingOnNonMessagePackets() {
        listener.processPacket(new Packet() {
            @Override
            public String toXML() {
                return null;
            }
        });

        verifyZeroInteractions(muc);
    }

    @Test
    public void sendsNothingOnEmptyBody() {
        given(message.getBody()).willReturn(null);

        listener.processPacket(message);

        verifyZeroInteractions(muc);
    }


    // TODO message body may contain "delay"
    @Test
    public void sendsNothingOnDelayedMessage() {
        given(message.getBody()).willReturn("Message body");
        given(message.toXML()).willReturn("...delay...");

        listener.processPacket(message);

        verifyZeroInteractions(muc);
    }

    @Test
    public void sendsNothingOnMessageFromRoom() {
        given(message.getBody()).willReturn("Message body");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org");

        listener.processPacket(message);

        verifyZeroInteractions(muc);
    }

    @Test
    public void sendsNothingOnMessageFromSelf() {
        given(message.getBody()).willReturn("Message body");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/minions");
        given(config.getMinionsNick()).willReturn("minions");

        listener.processPacket(message);

        verifyZeroInteractions(muc);
    }

    @Test
    public void passesRoomMessageToMinions() {
        String messageBody = "Message body";
        String fromNick = "bobby";
        given(message.getBody()).willReturn(messageBody);
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/" + fromNick);
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        listener.processPacket(message);

        verify(minions).onRoomMessage(messageBody, fromNick, room);
    }

    @Test
    public void showsHelp() throws XMPPException {
        given(message.getBody()).willReturn("!help");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        String cmd1 = "cmd1";
        String cmd1Help = "This is command 1";
        given(minions.get(cmd1)).willReturn(new Minion() {
            @Override
            public String getHelp() {
                return cmd1Help;
            }
        });

        String cmd2 = "cmd2";
        String cmd2Help = "Another command... cmd2";
        given(minions.get(cmd2)).willReturn(new Minion() {
            @Override
            public String getHelp() {
                return cmd2Help;
            }
        });

        given(minions.commandList()).willReturn(Arrays.asList(cmd1, cmd2));

        listener.processPacket(message);

        verify(muc).sendMessage(
                "\n" + config.getPrefix() + cmd1 + " " + cmd1Help +
                "\n" + config.getPrefix() + cmd2 + " " + cmd2Help
        );
    }

    @Test
    public void showsJars() throws XMPPException {
        given(message.getBody()).willReturn("!jars");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        MinionJar jar1 = mock(MinionJar.class);
        String jar1Name = "some-minion.jar";
        given(jar1.getName()).willReturn(jar1Name);
        given(jar1.getTimestamp()).willReturn(new Timestamp(2016 - 1900, 3 - 1, 12, 10, 54, 22, 0).getTime());

        MinionJar jar2 = mock(MinionJar.class);
        String jar2Name = "another-minion.jar";
        given(jar2.getName()).willReturn(jar2Name);
        given(jar2.getTimestamp()).willReturn(new Timestamp(2014 - 1900, 7 - 1, 3, 6, 12, 37, 0).getTime());

        List<MinionJar> jars = Arrays.asList(jar1, jar2);
        given(minions.getJars()).willReturn(jars);

        listener.processPacket(message);

        verify(muc).sendMessage(
                "\n" + jar1Name + ", last updated: " + "12-Mar-2016 10:54:22" +
                "\n" + jar2Name + ", last updated: " + "03-Jul-2014 06:12:37"
        );
    }

    @Test
    public void sendMessageWhenNoMinionFound() throws XMPPException {
        given(message.getBody()).willReturn("!dosomething with these args");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        given(minions.get("dosomething")).willReturn(null);

        listener.processPacket(message);

        verify(muc).sendMessage("No such minion: dosomething");
    }

    @Test
    public void callsMinion() throws XMPPException {
        given(message.getBody()).willReturn("!action arg1 arg2");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        Minion minion = mock(Minion.class);
        given(minions.get("action")).willReturn(minion);

        listener.processPacket(message);

        verify(minion).onCommandWrapper(room, "bobby", "arg1 arg2");
    }

    @Test
    public void callsMinionWhenNoArgs() throws XMPPException {
        given(message.getBody()).willReturn("!trolls");
        given(message.toXML()).willReturn("xml");
        given(message.getFrom()).willReturn("room@conference.server.org/bobby");
        given(config.getMinionsNick()).willReturn("minions");
        given(config.getPrefix()).willReturn("!");

        Minion minion = mock(Minion.class);
        given(minions.get("trolls")).willReturn(minion);

        listener.processPacket(message);

        verify(minion).onCommandWrapper(room, "bobby", "");
    }
}