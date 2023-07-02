package com.krish.physicssolver.controllers;

import com.krish.physicssolver.SceneType;
import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.components.RegisteredContainer;
import com.krish.physicssolver.equation.Equation;
import com.krish.physicssolver.equation.Part;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

public class RegisterController implements Initializable, RefreshableController {
    @FXML
    TextField equationTextField;

    @FXML
    Button equationSubmitButton;

    @FXML
    VBox registeredEquationsVBox;

    @FXML
    VBox registeredConstantsVBox;

    @FXML
    VBox registeredVariablesVBox;

    @FXML
    Button nextButton;

    @FXML
    MenuItem loadFromFileMenuItem;

    @FXML
    MenuItem loadFromFileHelpMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        equationSubmitButton.setOnAction(event -> {
            String equation = equationTextField.getText().trim().replace(" ", "");
            if (Equation.isValidEquation(equation)) {
                Equation parsedEquation = Equation.parseEquation(equation);
                if (parsedEquation.getVariables().size() == 0) {
                    equationTextField.setText("");
                    equationTextField.setPromptText("Equation Has No Variables");
                    return;
                }
                if (SolverApplication.isEquationUnique(parsedEquation)) {
                    equationTextField.setText("");
                    equationTextField.setPromptText("Enter Equation or Constant");
                    SolverApplication.registerEquation(parsedEquation);
                    addRegisteredEquation(parsedEquation);
                } else {
                    equationTextField.setText("");
                    equationTextField.setPromptText("Equation Already Registered");
                }
            } else {
                equationTextField.setText("");
                equationTextField.setPromptText("Invalid Equation");
            }
        });
        nextButton.setOnAction(event -> SolverApplication.setActiveScene(SceneType.INPUT));
        loadFromFileMenuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Select A Equations File (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extensionFilter);
            File equations = fileChooser.showOpenDialog(new Stage());
            if (equations != null) {
                try {
                    parseFileEquations(equations);
                } catch (Exception ignored) {
                }
            }
        });
        loadFromFileHelpMenuItem.setOnAction(event -> {
            try {
                createLoadHelpModal();
            } catch (Exception ignored) {}
        });
    }

    public void refresh() {
        SolverApplication.equations.forEach(this::addRegisteredEquation);
        updateRegisteredValues();
    }

    private void parseFileEquations(File equations) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(equations));

        String currentEquation;
        while ((currentEquation = fileReader.readLine()) != null) {
            currentEquation = currentEquation.trim().replace(" ", "");
            if (Equation.isValidEquation(currentEquation)) {
                Equation parsedEquation = Equation.parseEquation(currentEquation);
                if (parsedEquation.getVariables().size() == 0) continue;
                if (SolverApplication.isEquationUnique(parsedEquation)) {
                    SolverApplication.registerEquation(parsedEquation);
                    addRegisteredEquation(parsedEquation);
                }
            }
        }
    }

    private void createLoadHelpModal() throws IOException {
        FXMLLoader modalFXML = new FXMLLoader(SolverApplication.class.getResource("modals/Register_LoadHelp.fxml"));

        Scene scene = new Scene(modalFXML.load());
        scene.getStylesheets().add(SolverApplication.mainStylesheet);

        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Help - Loading Equations From A File");
        stage.initOwner(SolverApplication.primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    public void updateRegisteredValues() {
        registeredVariablesVBox.getChildren().clear();
        for (String variable : SolverApplication.variables) {
            Label registeredVariableLabel = new Label();
            registeredVariableLabel.getStyleClass().addAll("bold");
            registeredVariableLabel.setStyle("-fx-font-size: 15px;");
            registeredVariableLabel.setText(variable);
            registeredVariablesVBox.getChildren().add(registeredVariableLabel);
        }
        registeredConstantsVBox.getChildren().clear();
        for (String constant : SolverApplication.constants) {
            Label registeredConstantLabel = new Label();
            registeredConstantLabel.getStyleClass().addAll("bold");
            registeredConstantLabel.setStyle("-fx-font-size: 15px;");
            registeredConstantLabel.setText(constant + "=" + SolverApplication.values.get(constant).setScale(Part.DISPLAY_SCALE, RoundingMode.HALF_UP));
            registeredConstantsVBox.getChildren().add(registeredConstantLabel);
        }
    }

    private void addRegisteredEquation(Equation equation) {
        registeredEquationsVBox.getChildren().add(new RegisteredContainer(equation));
    }

    public void removeRegisteredEquation(RegisteredContainer display) {
        SolverApplication.unregisterEquation(display.getRegisteredEquation());
        registeredEquationsVBox.getChildren().remove(display);
    }

    public void cleanup() {
        registeredEquationsVBox.getChildren().clear();
        registeredConstantsVBox.getChildren().clear();
        registeredVariablesVBox.getChildren().clear();
    }
}
