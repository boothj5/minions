package com.boothj5.minions;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MinionsMapTest {

    @Test
    public void returnsEmptyCommandList() {
        MinionsMap minionsMap = new MinionsMap();

        List<String> command = minionsMap.getCommands();

        assertEquals(0, command.size());
    }

    @Test
    public void addsToCommandList() {
        MinionsMap minions = new MinionsMap();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;

            }

            @Override
            public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };
        minions.add("testcmd2", minion);

        List<String> commands = minions.getCommands();
        String command = commands.get(0);

        assertEquals("testcmd2", command);
    }

    @Test
    public void addsMinion() {
        MinionsMap minions = new MinionsMap();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;

            }

            @Override
            public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };
        minions.add("testcmd2", minion);

        Minion actualMinion = minions.get("testcmd2");

        assertEquals(minion, actualMinion);
    }

    @Test
    public void removesMinion() {
        MinionsMap minions = new MinionsMap();
        Minion minion = new Minion() {
            @Override
            public String getHelp() {
                return null;
            }

            @Override
            public void onCommand(MinionsRoom muc, String from, String message) throws MinionsException {

            }
        };

        minions.add("testcmd3", minion);
        assertEquals(minions.getCommands().size(), 1);
        minions.remove("testcmd3");

        assertEquals(minions.getCommands().size(), 0);
    }

}