package com.krish.physicssolver.controllers;

import com.krish.physicssolver.SceneType;
import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.equation.Equation;
import com.krish.physicssolver.equation.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SolveController implements Initializable, RefreshableController {
    @FXML
    VBox outputVBox;

    @FXML
    Button previousButton;

    public final Map<String, BigDecimal> solvedVariables = new HashMap<>();
    public final Map<String, Equation> equationUsed = new HashMap<>();

    @Override
    public void refresh() {
        for (Map.Entry<String, BigDecimal> entry : SolverApplication.values.entrySet()) {
            if (entry.getValue() != null) solvedVariables.put(entry.getKey(), entry.getValue());
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Equation equation : SolverApplication.equations) {
                ArrayList<String> missingVariables = equation.getMissingVariables(solvedVariables.keySet());
                if (missingVariables.size() == 1) {
                    solvedVariables.put(missingVariables.get(0), equation.solveForMissing(missingVariables.get(0), solvedVariables));
                    equationUsed.put(missingVariables.get(0), equation);
                    changed = true;
                }
            }
        }
        for (Map.Entry<String, BigDecimal> entry : solvedVariables.entrySet()) {
            String key = entry.getKey();
            Label solvedLabel = new Label();
            solvedLabel.getStyleClass().add("bold");
            solvedLabel.setText(entry.getKey() + ": " + entry.getValue().setScale(Part.DISPLAY_SCALE, RoundingMode.HALF_UP));
            solvedLabel.setOnMouseClicked(event -> {
                try {
                    createDerivationModal(key);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            outputVBox.getChildren().add(solvedLabel);
        }
    }

    public void createDerivationModal(String variable) throws IOException {
        FXMLLoader modalFXML = new FXMLLoader(SolverApplication.class.getResource("modals/Solve_DerivationModal.fxml"));

        Scene scene = new Scene(modalFXML.load());
        scene.getStylesheets().add(SolverApplication.mainStylesheet);

        Solve_DerivationModalController controller = modalFXML.getController();
        controller.deriveForVariable(variable);

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Derivation For " + variable);
        stage.initOwner(SolverApplication.primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    @Override
    public void cleanup() {
        outputVBox.getChildren().clear();
        solvedVariables.clear();
        equationUsed.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previousButton.setOnAction(event -> SolverApplication.setActiveScene(SceneType.INPUT));
    }
}
