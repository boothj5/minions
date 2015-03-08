package com.boothj5.minions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MinionsDir {
    private final File dir;

    MinionsDir(String minionsDir) {
        dir = new File(minionsDir);
    }

    List<MinionJar> listMinionJars() throws IOException {
        List<MinionJar> result = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                result.add(new MinionJar(file));
            }
        }

        return result;
    }
}
