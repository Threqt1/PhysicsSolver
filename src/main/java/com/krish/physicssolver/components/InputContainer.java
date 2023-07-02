package com.krish.physicssolver.components;

import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.equation.Part;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InputContainer extends HBox {
    final String variable;

    public InputContainer(String variable, BigDecimal initialValue, boolean editable) {
        super();
        this.variable = variable;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

        Label variableLabel = new Label();
        variableLabel.getStyleClass().addAll("bold");
        variableLabel.setStyle("-fx-font-size: 15px;");
        variableLabel.setText(variable);

        TextField textField = new TextField();
        textField.setPromptText("Enter Value For " + variable + " here");
        textField.setText(initialValue != null ? String.valueOf(initialValue.setScale(Part.DISPLAY_SCALE, RoundingMode.HALF_UP)) : "");
        textField.setEditable(editable);

        Button submitButton = new Button();
        submitButton.getStyleClass().add("bold");
        submitButton.setText("Set Value");
        submitButton.setDisable(!editable);
        submitButton.setOnAction(event -> {
            String textFieldValue = textField.getText().trim();
            if (textFieldValue.length() == 0) {
                SolverApplication.values.remove(variable);
                textField.setText("");
                return;
            }
            BigDecimal parsedValue = null;
            boolean isNumeric = true;
            try {
                Double.parseDouble(textFieldValue);
                parsedValue = new BigDecimal(textFieldValue).setScale(Part.CALCULATION_SCALE, RoundingMode.HALF_UP);
            } catch (Exception ignored) {
                isNumeric = false;
            }
            if (!isNumeric) {
                textField.setText("");
                textField.setPromptText("Invalid Value");
                return;
            }
            textField.setText(String.valueOf(parsedValue.setScale(Part.DISPLAY_SCALE, RoundingMode.HALF_UP)));
            SolverApplication.values.put(variable, parsedValue);
        });

        this.getChildren().addAll(variableLabel, textField, submitButton);
    }

    public String getVariable() {
        return variable;
    }
}
