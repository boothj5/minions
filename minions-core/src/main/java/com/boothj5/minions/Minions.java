package com.boothj5.minions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Minions {

    private final HashMap<String, Minion> map;

    public Minions() {
        this.map = new HashMap<>();
    }

    public List<String> getCommands() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(map.keySet());

        return result;
    }

    public Minion get(String command) {
        return map.get(command);
    }

    public void remove(String command) {
        Minion minionToRemove = map.get(command);
        minionToRemove.onRemove();
        map.remove(command);
    }

    public void add(String command, Minion minion) {
        map.put(command, minion);
    }
}
