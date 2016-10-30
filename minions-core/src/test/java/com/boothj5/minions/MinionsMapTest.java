package com.boothj5.minions;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MinionsMapTest {

    @Test
    public void returnsEmptyCommandList() {
        MinionsMap minionsMap = new MinionsMap();

        Set<String> command = minionsMap.keySet();

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
        minions.put("testcmd2", minion);

        Set<String> commands = minions.keySet();

        assertTrue(commands.contains("testcmd2"));
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
        minions.put("testcmd2", minion);

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

        minions.put("testcmd3", minion);
        assertEquals(minions.keySet().size(), 1);
        minions.remove("testcmd3");

        assertEquals(minions.keySet().size(), 0);
    }

}