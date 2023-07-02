package com.krish.physicssolver.controllers;

import com.krish.physicssolver.SceneType;
import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.components.DerivedContainer;
import com.krish.physicssolver.equation.Equation;
import javafx.fxml.FXML;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

public class Solve_DerivationModalController {
    @FXML
    VBox derivationVBox;

    static class PriorityQueueEquation implements Comparable<PriorityQueueEquation> {
        public final int priority;
        public final String derivation;
        public final String variable;

        public PriorityQueueEquation(int priority, String derivation, String variable) {
            this.priority = priority;
            this.derivation = derivation;
            this.variable = variable;
        }

        @Override
        public int compareTo(PriorityQueueEquation o) {
            return Integer.compare(priority, o.priority);
        }
    }

    final PriorityQueue<PriorityQueueEquation> priorityQueue = new PriorityQueue<>();
    final HashSet<String> derivedVariables = new HashSet<>();
    Map<String, Equation> equationUsed;
    Map<String, BigDecimal> solvedVariables;
    final Map<String, Integer> variableDepth = new HashMap<>();

    public void deriveForVariable(String variable) {
        equationUsed = ((SolveController) SolverApplication.controllers.get(SceneType.SOLVE)).equationUsed;
        solvedVariables = ((SolveController) SolverApplication.controllers.get(SceneType.SOLVE)).solvedVariables;
        recurseForDerivations(variable);
        int lastPriority = 0;
        while (!priorityQueue.isEmpty()) {
            PriorityQueueEquation equation = priorityQueue.poll();
            if (lastPriority != equation.priority) {
                lastPriority = equation.priority;
                derivationVBox.getChildren().add(new Separator());
            }
            derivationVBox.getChildren().add(new DerivedContainer(equation.variable, equation.derivation));
        }
    }

    private int recurseForDerivations(String variable) {
        if (!derivedVariables.add(variable)) return variableDepth.getOrDefault(variable, 0);
        if (SolverApplication.values.get(variable) != null) {
            priorityQueue.add(new PriorityQueueEquation(0, variable + "=" + solvedVariables.get(variable).setScale(5, RoundingMode.HALF_UP), variable));
            variableDepth.put(variable, 0);
            return 0;
        }
        int maxDepth = 1;
        Equation equation = equationUsed.get(variable);
        String displayEquation = equation.toString();
        StringBuilder derivation = new StringBuilder().append(displayEquation).append("\n");
        for (String var : equation.getVariables()) {
            if (var.equals(variable)) continue;
            displayEquation = displayEquation.replace(var, "(" + solvedVariables.get(var).setScale(5, RoundingMode.HALF_UP) + ")");
            maxDepth = Math.max(maxDepth, 1 + recurseForDerivations(var));
        }
        derivation.append(displayEquation).append("\n").append(variable).append("=").append(solvedVariables.get(variable).setScale(5, RoundingMode.HALF_UP));
        priorityQueue.add(new PriorityQueueEquation(maxDepth, derivation.toString(), variable));
        variableDepth.put(variable, maxDepth);
        return maxDepth;
    }
}
