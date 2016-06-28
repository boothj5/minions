package com.boothj5.minions.coster;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {
    private static final String CSV_PATH = System.getProperty("user.home") + "/.local/share/minions/transactions.csv";
    private final Map<String, Float> spenders = new HashMap<>();

    public Transactions() {
        File csvFile = new File(CSV_PATH);
        if (csvFile.exists()) {
            try {
                CSVReader reader = new CSVReader(new FileReader(CSV_PATH), ',');
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    spenders.put(nextLine[0], round(Float.parseFloat(nextLine[1])));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(String spender, Float amount) {
        if (spenders.containsKey(spender)) {
            spenders.put(spender, spenders.get(spender) + amount);
        } else {
            spenders.put(spender, amount);
        }

        save();
    }

    public Float get(String spender) {
        return spenders.get(spender);
    }

    public void clear(String spender) {
        if (spenders.containsKey(spender)) {
            spenders.remove(spender);
            save();
        }
    }

    public void clear() {
        spenders.clear();
        save();
    }

    public List<String> getSpenders() {
        return new ArrayList<>(spenders.keySet());
    }

    public int size() {
        return spenders.size();
    }

    public Map<String, Map<String, Float>> getOwers() {
        Map<String, Map<String, Float>> owers = new HashMap<>();
        for (String spender : getSpenders()) {
            Float spenderSpent = round(get(spender));
            Float spenderOwed = round(spenderSpent / size());
            for (String ower : getSpenders()) {
                if (!ower.equals(spender)) {
                    if (owers.containsKey(ower)) {
                        Map<String, Float> owes = owers.get(ower);
                        owes.put(spender, spenderOwed);
                        owers.put(ower, owes);
                    } else {
                        Map<String, Float> owes = new HashMap<>();
                        owes.put(spender, spenderOwed);
                        owers.put(ower, owes);
                    }
                }
            }
        }
        return owers;
    }

    public Map<String, Map<String, Float>> getReducedOwers() {
        Map<String, Map<String, Float>> owers = getOwers();
        Map<String, Map<String, Float>> reducedOwers = new HashMap<>();
        for (String ower : owers.keySet()) {
            for (String spender : owers.get(ower).keySet()) {
                Float owerOwesSpender = owers.get(ower).get(spender);
                Float spenderOwesOwer = owers.get(spender).get(ower);

                if (owerOwesSpender > spenderOwesOwer) {
                    Float reduced = owerOwesSpender - spenderOwesOwer;
                    if (reducedOwers.containsKey(ower)) {
                        Map<String, Float> owes = reducedOwers.get(ower);
                        owes.put(spender, reduced);
                        reducedOwers.put(ower, owes);
                    } else {
                        Map<String, Float> owes = new HashMap<>();
                        owes.put(spender, reduced);
                        reducedOwers.put(ower, owes);
                    }
                }
            }
        }

        return reducedOwers;
    }

    public static float round(float d) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private void save() {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(CSV_PATH), ',');
            for (String spender : spenders.keySet()) {
                String amount = String.format("%.2f", spenders.get(spender));
                String[] entry = { spender, amount };
                writer.writeNext(entry);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


