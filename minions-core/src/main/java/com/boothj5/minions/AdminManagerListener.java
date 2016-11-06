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

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

import java.util.Map;

class AdminManagerListener implements ChatManagerListener {

    private final MinionsConfiguration config;
    private final Map<String, MinionsRoom> rooms;

    AdminManagerListener(MinionsConfiguration config, Map<String, MinionsRoom> rooms) {
        this.config = config;
        this.rooms = rooms;
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally) {
            chat.addMessageListener(new AdminMessageListener(config, rooms));
        }
    }
}
