package com.boothj5.minions.coster;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.boothj5.minions.coster.Transactions.round;

public class CosterMinion extends Minion {
    private static final String HELP = "help";
    private static final String SPEND = "spend";
    private static final String SPENDFOR = "spendfor";
    private static final String CLEAR = "clear";
    private static final String RESET = "reset";
    private static final String SHOW = "show";
    private static final String SPLIT = "split";
    private static final String SPLIT2 = "split2";

    private Transactions transactions = new Transactions();

    public CosterMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return " - Split costs, see help for more information.";
    }

    @Override
    public void onCommand(String from, String message) {
        String[] tokens = StringUtils.split(message, " ");
        String command = tokens[0];
        StringBuffer out;

        switch (command) {
            case HELP:
                String help =
                    "\n" + "help - Show this help." +
                    "\n" + "spend <amount> - Add an amount to your current total" +
                    "\n" + "spendfor <nick> <amount> - Add an amount to another person's total" +
                    "\n" + "clear - Clear your current amount" +
                    "\n" + "reset - Reset all spenders" +
                    "\n" + "show - Show current spenders" +
                    "\n" + "split - Work out what people owe each other" +
                    "\n" + "split2 - Work out what people owe each other, and minimise transactions";
                room.sendMessage(help);
                break;
            case SPEND:
                if (tokens.length < 2) {
                    room.sendMessage(from + ": You must specify an amount.");
                } else {
                    Float amount = round(Float.parseFloat(tokens[1]));
                    transactions.add(from, amount);
                    room.sendMessage(from + ": " + String.format("%.2f", transactions.get(from)));
                }
                break;
            case SPENDFOR:
                if (tokens.length < 3) {
                    room.sendMessage(from + ": You must specify a user and amount.");
                } else {
                    String user = tokens[1];
                    Float amount = round(Float.parseFloat(tokens[2]));
                    transactions.add(user, amount);
                    room.sendMessage(user + ": " + String.format("%.2f", transactions.get(user)));
                }
                break;
            case CLEAR:
                transactions.clear(from);
                room.sendMessage(from + ": 0.00");
                break;
            case RESET:
                transactions.clear();
                room.sendMessage("Cleared all amounts.");
            case SHOW:
                out = new StringBuffer("\n");
                Float total = 0.0f;
                for (String spender : transactions.getSpenders()) {
                    out.append(spender)
                        .append(": ")
                        .append(String.format("%.2f", transactions.get(spender)))
                        .append("\n");
                    total += transactions.get(spender);
                }

                out.append("\nTOTAL: ").append(String.format("%.2f", total));
                room.sendMessage(out.toString());
                break;
            case SPLIT:
                if (transactions.size() > 1) {
                    Map<String, Map<String, Float>> owers = transactions.getOwers();
                    showOwed(owers);
                } else {
                    room.sendMessage("No one owes anyone anything.");
                }
                break;
            case SPLIT2:
                if (transactions.size() > 1) {
                    Map<String, Map<String, Float>> owers = transactions.getReducedOwers();
                    showOwed(owers);
                } else {
                    room.sendMessage("No one owes anyone anything.");
                }
                break;
            default:
                room.sendMessage("Invalid coster command.");
                break;
        }
    }

    private void showOwed(Map<String, Map<String, Float>> owers) {
        StringBuffer out;
        out = new StringBuffer("\n");
        for (String ower : owers.keySet()) {
            out.append(ower).append(" owes:\n");
            for (String spender : owers.get(ower).keySet()) {
                out.append("  ")
                    .append(spender)
                    .append(": ")
                    .append(String.format("%.2f", owers.get(ower).get(spender)))
                    .append("\n");
            }
        }
        room.sendMessage(out.toString());
    }
}
