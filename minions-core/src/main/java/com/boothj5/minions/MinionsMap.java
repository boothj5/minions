/*
 * Copyright 2015 - 2016 James Booth
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

import java.util.*;

class MinionsMap implements Map<String, Minion> {
    private final HashMap<String, Minion> map;

    MinionsMap() {
        this.map = new HashMap<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Minion get(Object key) {
        return map.get(key);
    }

    @Override
    public Minion put(String key, Minion value) {
        return map.put(key, value);
    }

    @Override
    public Minion remove(Object key) {
        Minion minionToRemove = map.get(key);
        minionToRemove.onRemove();
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Minion> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Minion> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Minion>> entrySet() {
        return map.entrySet();
    }
}
