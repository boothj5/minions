package com.boothj5.minions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Minions {

    private final HashMap<String, Minion> map;

    Minions() {
        this.map = new HashMap<>();
    }

    List<String> getCommands() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(map.keySet());

        return result;
    }

    Minion get(String command) {
        return map.get(command);
    }

    void remove(String command) {
        Minion minionToRemove = map.get(command);
        minionToRemove.onRemove();
        map.remove(command);
    }

    void add(String command, Minion minion) {
        map.put(command, minion);
    }
}
