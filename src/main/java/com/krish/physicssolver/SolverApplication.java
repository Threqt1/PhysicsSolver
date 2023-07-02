package com.krish.physicssolver;

import com.krish.physicssolver.controllers.RefreshableController;
import com.krish.physicssolver.controllers.RegisterController;
import com.krish.physicssolver.equation.Equation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * TODO:
 * ADD APP1 EQUATIONS PRESET
 */
public class SolverApplication extends Application {
    static final String VERSION = "1.0.0";
    public static Stage primaryStage;
    public static final String mainStylesheet = Objects.requireNonNull(SolverApplication.class.getResource("stylesheets/MainStylesheet.css")).toExternalForm();
    public static final HashMap<SceneType, Scene> scenes = new HashMap<>();
    public static final HashMap<SceneType, RefreshableController> controllers = new HashMap<>();
    public static final ArrayList<Equation> equations = new ArrayList<>();
    public static final HashMap<String, ArrayList<Equation>> variableEquations = new HashMap<>();
    public static final HashMap<String, BigDecimal> values = new HashMap<>();
    public static final HashSet<String> variables = new HashSet<>();
    public static final HashSet<String> constants = new HashSet<>();
    public static SceneType ACTIVE_SCENE = SceneType.REGISTER;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SolverApplication.primaryStage = primaryStage;
        primaryStage.setTitle("Physics Solver - v" + VERSION);

        FXMLLoader loader;
        Parent root;
        Scene scene;
        //Register
        loader = new FXMLLoader(getClass().getResource("Register.fxml"));
        root = loader.load();
        controllers.put(SceneType.REGISTER, loader.getController());
        scene = new Scene(root);
        scene.getStylesheets().add(mainStylesheet);
        scenes.put(SceneType.REGISTER, scene);
        //Input
        loader = new FXMLLoader(getClass().getResource("Input.fxml"));
        root = loader.load();
        controllers.put(SceneType.INPUT, loader.getController());
        scene = new Scene(root);
        scene.getStylesheets().add(mainStylesheet);
        scenes.put(SceneType.INPUT, scene);
        //Solve
        loader = new FXMLLoader(getClass().getResource("Solve.fxml"));
        root = loader.load();
        controllers.put(SceneType.SOLVE, loader.getController());
        scene = new Scene(root);
        scene.getStylesheets().add(mainStylesheet);
        scenes.put(SceneType.SOLVE, scene);

        SolverApplication.setActiveScene(SceneType.REGISTER);
        primaryStage.show();
    }

    public static void registerEquation(Equation equation) {
        if (equation.getVariables().size() == 1) {
            String variable = equation.getVariables().get(0);
            constants.add(variable);
            variables.remove(variable);
            values.put(variable, equation.solveForMissing(variable, new HashMap<>()));
        } else {
            for (String variable : equation.getVariables()) {
                variableEquations.computeIfAbsent(variable, key -> new ArrayList<>()).add(equation);
                if (constants.contains(variable)) continue;
                variables.add(variable);
            }
        }
        equations.add(equation);
        if (ACTIVE_SCENE == SceneType.REGISTER) ((RegisterController) controllers.get(SceneType.REGISTER)).updateRegisteredValues();
    }

    public static void unregisterEquation(Equation equation) {
        if (equation.getVariables().size() == 1) {
            String variable = equation.getVariables().get(0);
            constants.remove(variable);
            values.remove(variable);
        } else {
            for (String variable : equation.getVariables()) {
                variableEquations.computeIfAbsent(variable, key -> new ArrayList<>()).remove(equation);
                if (variableEquations.get(variable).size() == 0) variables.remove(variable);
            }
        }
        equations.remove(equation);
        if (equation.getVariables().size() == 1) {
            String variable = equation.getVariables().get(0);
            if (variableEquations.computeIfAbsent(variable, key -> new ArrayList<>()).size() > 0) variables.add(variable);
        }
        if (ACTIVE_SCENE == SceneType.REGISTER) ((RegisterController) controllers.get(SceneType.REGISTER)).updateRegisteredValues();
    }

    public static boolean isEquationUnique(Equation equation) {
        if (equation.getVariables().size() == 1 && constants.contains(equation.getVariables().get(0))) return false;
        for (Equation eq : equations) {
            if (eq.equals(equation)) return false;
        }
        return true;
    }

    public static void setActiveScene(SceneType sceneType) {
        controllers.get(ACTIVE_SCENE).cleanup();
        ACTIVE_SCENE = sceneType;
        controllers.get(ACTIVE_SCENE).refresh();
        primaryStage.setScene(scenes.get(ACTIVE_SCENE));
    }
}
