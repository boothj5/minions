package com.boothj5.commandbot;

import com.boothj5.commandbot.api.CommandBotPlugin;

import java.util.*;

public class PluginStore {
    private final Map<String, CommandBotPlugin> plugins;

    public PluginStore() {
        this.plugins = new HashMap<>();
    }

    public void register(CommandBotPlugin plugin) {
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

    public CommandBotPlugin get(String command) {
        return plugins.get(command);
    }
}
