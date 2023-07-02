package com.krish.physicssolver.controllers;

import com.krish.physicssolver.SceneType;
import com.krish.physicssolver.SolverApplication;
import com.krish.physicssolver.components.InputContainer;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class InputController implements Initializable, RefreshableController {
    @FXML
    VBox inputVBox;

    @FXML
    TextField variableSearchTextField;

    @FXML
    Button previousButton;

    @FXML
    Button nextButton;

    final ArrayList<InputContainer> containers = new ArrayList<>();
    final ChangeListener<String> searchFieldListener = (observable, oldValue, newValue) -> {
        inputVBox.getChildren().clear();
        if (newValue.trim().equals("")) {
            inputVBox.getChildren().addAll(containers);
        } else {
            inputVBox.getChildren().addAll(containers.stream().filter(r -> r.getVariable().contains(newValue.trim())).collect(Collectors.toSet()));
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previousButton.setOnAction(event -> SolverApplication.setActiveScene(SceneType.REGISTER));
        nextButton.setOnAction(event -> SolverApplication.setActiveScene(SceneType.SOLVE));
        variableSearchTextField.textProperty().addListener(searchFieldListener);
    }

    public void refresh() {
        for (String constantEntry : SolverApplication.constants) {
            containers.add(new InputContainer(constantEntry, SolverApplication.values.getOrDefault(constantEntry, null), false));
        }
        for (String variableEntry : SolverApplication.variables) {
            containers.add(new InputContainer(variableEntry, SolverApplication.values.getOrDefault(variableEntry, null), true));
        }
        variableSearchTextField.setText("");
        searchFieldListener.changed(variableSearchTextField.textProperty(), "", "");
    }

    public void cleanup() {
        inputVBox.getChildren().clear();
        containers.clear();
    }
}
