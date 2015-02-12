package com.boothj5.commandbot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public CommandBotPlugin get(String command) {
        return plugins.get(command);
    }

    public String getHelp() {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append(":list - ");
        result.append("Show this list.");
        result.append("\n");
        List<Object> commands = Arrays.asList(plugins.keySet().toArray());
        for (Object command : commands) {
            String commandStr = (String)command;
            String help = plugins.get(command).getHelp();
            result.append(":");
            result.append(commandStr);
            result.append(" - ");
            result.append(help);
            result.append("\n");
        }
        return result.toString();
    }
}
