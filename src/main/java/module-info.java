module com.krish.physicssolver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.krish.physicssolver to javafx.fxml;
    opens com.krish.physicssolver.controllers to javafx.fxml;
    exports com.krish.physicssolver;
    exports com.krish.physicssolver.equation;
    exports com.krish.physicssolver.controllers;
    exports com.krish.physicssolver.components;
}