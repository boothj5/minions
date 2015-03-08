package com.boothj5.minions;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

class MinionsMain {
    public static void main(String[] args) throws MinionsException {
        if (args.length < 1) {
            throw new MinionsException("No configuration file specified.");
        } else {
            try {
                Yaml yaml = new Yaml();
                InputStream is = new FileInputStream(new File(args[0]));
                Map<String, Map<String, Object>> yamlConfig = (Map<String, Map<String, Object>>)yaml.load(is);
                MinionsConfiguration minionsConfiguration = new MinionsConfiguration(yamlConfig);
                final MinionsRunner minionsRunner = new MinionsRunner(minionsConfiguration);
                minionsRunner.run();
            } catch (FileNotFoundException e) {
                throw new MinionsException("Could not find configuration file: " + args[0]);
            }
        }
    }
}
