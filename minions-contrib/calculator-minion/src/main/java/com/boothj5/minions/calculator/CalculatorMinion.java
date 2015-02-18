package com.boothj5.minions.calculator;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsException;
import com.boothj5.minions.MinionsRoom;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

public class CalculatorMinion extends Minion {

    @Override
    public String getHelp() {
        return "[expression] - Calculate result of evaluating expression.";
    }

    @Override
    public void onMessage(MinionsRoom muc, String from, String message) throws MinionsException {
        try {
            Expression expression = new ExpressionBuilder(message).build();
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
