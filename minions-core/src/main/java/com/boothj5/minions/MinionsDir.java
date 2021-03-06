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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class MinionsDir {
    private final File dir;

    MinionsDir(String minionsDir) {
        dir = new File(minionsDir);
    }

    List<MinionJar> listMinionJars() {
        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

        List<MinionJar> jars = Arrays.stream(files)
            .map(MinionJar::new)
            .collect(Collectors.toList());

        return jars;
    }
}
