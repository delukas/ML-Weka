package de.lukas.mlweka;

import de.lukas.mlweka.ui.DrawingCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        DrawingCanvas drawingCanvas = new DrawingCanvas();

        Scene scene = new Scene(drawingCanvas, 900, 500);
        VBox.setVgrow(drawingCanvas, Priority.ALWAYS);
        HBox.setHgrow(drawingCanvas, Priority.ALWAYS);
        stage.setScene(scene);
        stage.setTitle("ML-Weka");
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}