package com.boothj5.minions;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MinionsTest {

    @Test
    public void returnsEmptyCommandList() {
        Minions minions = new Minions();

        List<String> command = minions.getCommands();

        assertEquals(0, command.size());
    }

    @Test
    public void addsToCommandList() {
        Minions minions = new Minions();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;

            }

            @Override
            public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };
        minions.add("testcmd2", minion);

        List<String> commands = minions.getCommands();
        String command = commands.get(0);

        assertEquals("testcmd2", command);
    }

    @Test
    public void addsMinion() {
        Minions minions = new Minions();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;

            }

            @Override
            public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };
        minions.add("testcmd2", minion);

        Minion actualMinion = minions.get("testcmd2");

        assertEquals(minion, actualMinion);
    }

    @Test
    public void removesMinion() {
        Minions minions = new Minions();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;
            }

            @Override
            public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };

        minions.add("testcmd3", minion);
        assertEquals(minions.getCommands().size(), 1);
        minions.remove("testcmd3");

        assertEquals(minions.getCommands().size(), 0);
    }

}