/*
 * Copyright 2015 James Booth
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
