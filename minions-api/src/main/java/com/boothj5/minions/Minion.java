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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Minion {
    private static final Logger LOG = LoggerFactory.getLogger(Minion.class);

    public abstract String getHelp();
    public abstract void onMessage(MinionsRoom muc, String from, String message) throws MinionsException;

    final void onMessageWrapper(MinionsRoom muc, String from, String message) {
        try {
            onMessage(muc, from, message);
        } catch (RuntimeException rte) {
            LOG.error("Minions RuntimeException", rte);
        } catch (MinionsException me) {
            LOG.error("MinionsException", me);
        }
    }

    public void onRemove() {

    }
}
