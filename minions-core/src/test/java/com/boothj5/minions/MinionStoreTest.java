package com.boothj5.minions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URLClassLoader;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MinionStoreTest {

    @Mock
    MinionsConfiguration config;

    @Mock
    MinionsRoom room;

    @Mock
    MinionsDir dir;

    @Mock
    MinionJar jar1;

    @Mock
    MinionJar jar2;

    @Mock
    Minion minion1;

    @Mock
    Minion minion2;

    MinionStore store;

    @Before
    public void setup() {
        reset(config);
        reset(room);
        reset(dir);
        reset(jar1);
        reset(jar2);
        reset(minion1);
        reset(minion2);

        given(config.getRefreshSeconds()).willReturn(0);
        given(config.getPrefix()).willReturn("!");

        given(jar1.getName()).willReturn("minion1.jar");
        given(jar1.getCommand()).willReturn("minion1");
        given(jar1.getTimestampFormat()).willReturn("21-May-2016 13:27:52");
        given(jar1.loadMinionClass(any(URLClassLoader.class))).willReturn(minion1);

        given(jar2.getName()).willReturn("minion2.jar");
        given(jar2.getCommand()).willReturn("minion2");
        given(jar2.getTimestampFormat()).willReturn("12-Dec-2016 01:00:12");
        given(jar2.loadMinionClass(any(URLClassLoader.class))).willReturn(minion2);

        given(dir.listMinionJars()).willReturn(Arrays.asList(jar1, jar2));

        given(minion1.getHelp()).willReturn("help for first minion");
        given(minion2.getHelp()).willReturn("help for second minion");

        store = new MinionStore(dir, config, room);
    }

    @Test
    public void sendsHelpToRoom() throws InterruptedException {
        store.onHelp();

        verify(room).sendMessage("\n" +
            "!minion1 help for first minion\n" +
            "!minion2 help for second minion");
    }

    @Test
    public void sendsJarsToRoom() {
        store.onJars();

        verify(room).sendMessage("\n" +
            "minion2.jar, last updated: 12-Dec-2016 01:00:12\n" +
            "minion1.jar, last updated: 21-May-2016 13:27:52");
    }

    @Test
    public void callsOnMessage() {
        store.onMessage("Hello", "bobby");

        verify(minion1).onMessage(room, "bobby", "Hello");
        verify(minion2).onMessage(room, "bobby", "Hello");
    }

    @Test
    public void callsOnCommand() {
        store.onCommand("!minion2 test args", "bruce");

        verify(minion2).onCommand(room, "bruce", "test args");
    }
}