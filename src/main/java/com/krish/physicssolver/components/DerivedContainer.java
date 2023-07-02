package com.krish.physicssolver.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class DerivedContainer extends VBox {
    public DerivedContainer(String variable, String derivation) {
        super(10);
        this.setAlignment(Pos.CENTER);

        Label variableLabel = new Label();
        variableLabel.getStyleClass().add("bold");
        variableLabel.setStyle("-fx-font-size: 15px;");
        variableLabel.setText(variable);

        VBox derivationVBox = new VBox();
        derivationVBox.setAlignment(Pos.CENTER);
        for (String part : derivation.split("\n")) {
            Label derivedLabel = new Label();
            derivedLabel.setText(part);
            derivedLabel.setTextAlignment(TextAlignment.CENTER);
            derivationVBox.getChildren().add(derivedLabel);
        }

        this.getChildren().addAll(variableLabel, derivationVBox);
    }
}
