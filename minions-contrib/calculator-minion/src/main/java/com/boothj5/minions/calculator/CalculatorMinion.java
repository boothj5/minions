package com.boothj5.minions.calculator;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

public class CalculatorMinion extends Minion {
    private final String COMMAND = "calc";

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String getHelp() {
        return COMMAND + " [expression] - Calculate result of evaluating expression.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            String expressionStr = message.substring(6);
            Expression expression = new ExpressionBuilder(expressionStr).build();
            ValidationResult validationResult = expression.validate();
            if (!validationResult.isValid()) {
                muc.sendMessage(from + ": " + validationResult.getErrors());
            } else {
                double result = expression.evaluate();
                muc.sendMessage(from + ": " + result);
            }
        } catch (RuntimeException rte) {
            muc.sendMessage(from + ": Invalid usage.");
        }
    }
}
