package com.boothj5.minions.calculator;

import com.boothj5.minions.Minion;
import com.boothj5.minions.MinionsRoom;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;

public class CalculatorMinion extends Minion {

    public CalculatorMinion(MinionsRoom room) {
        super(room);
    }

    @Override
    public String getHelp() {
        return "[expression] - Calculate result of evaluating expression.";
    }

    @Override
    public void onCommand(String from, String message) {
        try {
            Expression expression = new ExpressionBuilder(message).build();
            ValidationResult validationResult = expression.validate();
            if (!validationResult.isValid()) {
                room.sendMessage(from + ": " + validationResult.getErrors());
            } else {
                double result = expression.evaluate();
                room.sendMessage(from + ": " + result);
            }
        } catch (RuntimeException rte) {
            room.sendMessage(from + ": Invalid usage.");
        }
    }
}
