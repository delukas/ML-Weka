package de.lukas.mlweka;

import de.lukas.mlweka.ui.DrawingCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        DrawingCanvas drawingCanvas = new DrawingCanvas();

        TabPane tabPane = new TabPane();
        Tab testTab = new Tab("Daten prüfen", drawingCanvas);
        Tab dataTab = new Tab("Daten erstellen", new Text("Test"));
        testTab.setClosable(false);
        dataTab.setClosable(false);

        tabPane.getTabs().add(testTab);
        tabPane.getTabs().add(dataTab);
        Scene scene = new Scene(tabPane, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("ML-Weka");
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}