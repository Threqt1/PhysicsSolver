package com.krish.physicssolver.components;

import com.krish.physicssolver.SceneType;
import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.controllers.RegisterController;
import com.krish.physicssolver.equation.Equation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RegisteredContainer extends HBox {
    final Equation registeredEquation;

    public RegisteredContainer(Equation registeredEquation) {
        super();
        this.registeredEquation = registeredEquation;

        this.setAlignment(Pos.CENTER);
        this.setSpacing(10.0);

        Label registeredEquationLabel = new Label();
        registeredEquationLabel.setStyle("-fx-font-size: 15px;");
        registeredEquationLabel.setText(registeredEquation.toString());

        Button deleteEquationButton = new Button();
        deleteEquationButton.getStyleClass().add("bold");
        deleteEquationButton.setText("Delete");

        deleteEquationButton.setOnAction(event -> {
            if (SolverApplication.ACTIVE_SCENE == SceneType.REGISTER) ((RegisterController) SolverApplication.controllers.get(SceneType.REGISTER)).removeRegisteredEquation(this);
        });

        this.getChildren().addAll(registeredEquationLabel, deleteEquationButton);
    }

    public Equation getRegisteredEquation() {
        return registeredEquation;
    }
}
