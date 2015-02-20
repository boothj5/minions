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
    private final JLanguageTool langTool;

    public SpellCheckerMinion() throws IOException {
        langTool = new JLanguageTool(new BritishEnglish());
        langTool.activateDefaultPatternRules();
    }

    @Override
    public String getHelp() {
        return " text - Spell check the given line, and show suggestions.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            List<RuleMatch> matches = langTool.check(message);
            String result = "";

            for (RuleMatch match : matches) {
                if (!"Capitalization".equals(match.getRule().getCategory().toString())) {
                    String word = message.substring(match.getColumn() - 1, match.getEndColumn() - 1);
                    result += "\n" +
                            match.getRule().getCategory().toString() + ": " + word + "\n" +
                            "Suggestions: " + match.getSuggestedReplacements() + "\n";
                }
            }
            if (!result.equals("")) {
                muc.sendMessage(result);
            } else {
                muc.sendMessage("No suggestions.");
            }
        } catch (IOException e) {
            LOG.debug("Failed to initialise spell checker.");
            throw new MinionsException(e);
        }
    }
}
