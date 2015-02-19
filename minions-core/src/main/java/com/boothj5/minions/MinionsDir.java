package com.boothj5.minions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinionsDir {
    final File dir;

    public MinionsDir(String minionsDir) {
        dir = new File(minionsDir);
    }

    public List<MinionJar> listMinionJars() throws IOException {
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
