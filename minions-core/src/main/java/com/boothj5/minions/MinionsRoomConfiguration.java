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

import java.util.Optional;

class MinionsRoomConfiguration {
    private final String jid;
    private final String nick;
    private final String password;

    MinionsRoomConfiguration(String jid, String nick, String password) {
        if (jid == null) {
            throw new MinionsException("Missing configuration property: room.jid");
        }

        this.jid = jid;
        this.nick = nick == null ? "minions" : nick;
        this.password = password;
    }

    String getJid() {
        return jid;
    }

    String getNick() {
        return nick;
    }

    Optional<String> getPassword() {
        return password == null ? Optional.empty() : Optional.of(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinionsRoomConfiguration that = (MinionsRoomConfiguration) o;

        if (!jid.equals(that.jid)) return false;
        if (!nick.equals(that.nick)) return false;
        return password != null ? password.equals(that.password) : that.password == null;

    }

    @Override
    public int hashCode() {
        int result = jid.hashCode();
        result = 31 * result + nick.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
