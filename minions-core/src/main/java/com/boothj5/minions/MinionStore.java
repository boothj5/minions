package com.boothj5.minions;

import com.boothj5.minions.api.Minion;

import java.util.*;

public class MinionStore {
    private final Map<String, Minion> plugins;

    public MinionStore() {
        this.plugins = new HashMap<>();
    }

    public void register(Minion plugin) {
        plugins.put(plugin.getCommand(), plugin);
    }

    public boolean exists(String command) {
        return plugins.containsKey(command);
    }

    public List<String> commandList() {
        List<String> result = new ArrayList<>();
        Set<String> commandSet = plugins.keySet();

        for (String command: commandSet) {
            result.add(command);
        }

        return result;
    }

    public Minion get(String command) {
        return plugins.get(command);
    }
}
