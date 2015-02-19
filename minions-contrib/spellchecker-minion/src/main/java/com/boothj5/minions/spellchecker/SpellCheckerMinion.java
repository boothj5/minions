package com.boothj5.minions.spellchecker;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SpellCheckerMinion extends Minion {
    private static final Logger LOG = LoggerFactory.getLogger(SpellCheckerMinion.class);

    @Override
    public String getHelp() {
        return " text - Spell check the given line, and show suggestions.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
            langTool.activateDefaultPatternRules();
            List<RuleMatch> matches = langTool.check(message);

            for (RuleMatch match : matches) {
                String result =
                        "\n" +
                        "Potential error at line " + match.getLine() +
                        ", column " + match.getColumn() +
                        ": " + match.getMessage() +
                        "\n" +
                        "Suggested correction: " + match.getSuggestedReplacements();

                muc.sendMessage(result);
            }
        } catch (IOException e) {
            LOG.debug("Failed to initialise spell checker.");
            throw new MinionsException(e);
        }
    }
}
