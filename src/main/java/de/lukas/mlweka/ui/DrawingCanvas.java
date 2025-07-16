package de.lukas.mlweka.ui;

import de.lukas.mlweka.services.WekaService;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

public class DrawingCanvas extends VBox {

    private final int GRID_SIZE = 28;
    private final int CANVAS_SIZE = GRID_SIZE*15;
    private final int CELL_SIZE = CANVAS_SIZE / GRID_SIZE;

    private final Canvas canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();
    private double[][] imageData = new double[GRID_SIZE][GRID_SIZE];
    private WekaService wekaService;

    public DrawingCanvas() {

        Button resetButton = new Button("Canvas zurücksetzen");
        resetButton.setOnAction(_ -> reset());

        Button validateButton = new Button("Canvas prüfen");
        validateButton.setDisable(true);
        validateButton.setOnAction(_ -> {
            centerImage();
            int predictedNumber = wekaService.predictNumber(getImageDataFlat());
            System.out.println(predictedNumber);
        });

        Button initWekaButton = new Button(new File(WekaService.MODEL_PATH).exists() ? "Modell laden" : "Modell trainieren");
        initWekaButton.setOnAction(_ -> {
            wekaService = new WekaService();
            validateButton.setDisable(false);
            initWekaButton.setDisable(true);

        });

        canvas.setCursor(Cursor.CROSSHAIR);

        canvas.setOnMousePressed(e -> drawAt(e.getX(), e.getY()));

        canvas.setOnMouseDragged(e -> drawAt(e.getX(), e.getY()));

        this.getChildren().addAll(resetButton, initWekaButton, canvas, validateButton);
        reset();
    }

    private void drawAt(double x, double y) {
        int gridX = (int) (x / CELL_SIZE);
        int gridY = (int) (y / CELL_SIZE);

        if (gridX < 0 || gridY < 0 || gridX >= GRID_SIZE || gridY >= GRID_SIZE) return;

        int radius = 2;
        double maxDistance = radius + 0.5;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int nx = gridX + dx;
                int ny = gridY + dy;

                if (nx < 0 || ny < 0 || nx >= GRID_SIZE || ny >= GRID_SIZE) continue;

                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance > maxDistance) continue;

                double intensity = Math.pow(1.0 - (distance / maxDistance), 2.0);

                imageData[ny][nx] = Math.min(1.0, imageData[ny][nx] + intensity);
            }
        }

        drawImageData();
    }

    private void drawImageData() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                double intensity = imageData[y][x];
                if (intensity > 0) {
                    gc.setFill(Color.gray(1.0 - intensity));
                    gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        for (int i = 0; i <= GRID_SIZE; i++) {
            gc.strokeLine(i * CELL_SIZE, 0, i * CELL_SIZE, CANVAS_SIZE);
            gc.strokeLine(0, i * CELL_SIZE, CANVAS_SIZE, i * CELL_SIZE);
        }
    }

    private void reset() {
        Arrays.stream(imageData).forEach(l -> Arrays.fill(l, 0.0));
        drawImageData();
    }

    private void centerImage(){

        int top = 0;
        int bottom = 0;
        int left = 0;
        int right = 0;

        for (int i = 0; i < imageData.length; i++) {
            final int pointer = i;

            top = top == pointer && Arrays.stream(imageData[pointer]).allMatch(v -> v == 0) ? pointer + 1 : top;
            bottom = bottom == pointer && Arrays.stream(imageData[imageData.length-1-pointer]).allMatch(v -> v == 0) ? pointer + 1 : bottom;

            left = left == pointer && IntStream.range(0, imageData.length).allMatch(y -> imageData[y][pointer] == 0) ? pointer + 1 : left;
            right = right == pointer && IntStream.range(0, imageData.length).allMatch(y -> imageData[y][imageData.length-1-pointer] == 0) ? pointer + 1 : right;
        }

        final int shiftY = (bottom - top) / 2;
        final int shiftX = (right - left) / 2;

        double[][] centered = new double[GRID_SIZE][GRID_SIZE];
        for (int y = 0; y < GRID_SIZE; y++) {
            int newY = y + shiftY;
            if (newY < 0 || newY >= GRID_SIZE) continue;

            for (int x = 0; x < GRID_SIZE; x++) {
                int newX = x + shiftX;
                if (newX < 0 || newX >= GRID_SIZE) continue;

                centered[newY][newX] = imageData[y][x];
            }
        }

        imageData = centered;
        drawImageData();

    }

    public double[] getImageDataFlat() {
        return Arrays.stream(imageData)
                .flatMapToDouble(Arrays::stream)
                .toArray();
    }
}
