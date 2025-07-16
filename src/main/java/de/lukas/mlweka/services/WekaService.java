package de.lukas.mlweka.services;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import java.io.File;

public class WekaService {

    public static final String BASE_PATH = "./src/main/resources/" ;
    public static final String TRAIN_DATA_PATH = BASE_PATH + "mnist_train.arff";
    public static final String TEST_DATA_PATH = BASE_PATH + "mnist_test.arff";
    public static final String MODEL_PATH = BASE_PATH + "model.model";
    private MultilayerPerceptron model;

    public WekaService() {
        File modelFile = new File(MODEL_PATH);
        if(modelFile.exists() && !modelFile.isDirectory()) {
            System.out.println("Es wurde ein Modell gefunden unter: " + MODEL_PATH);
            loadModel();
            return;
        }

        trainModel();
    }

    public void trainModel(){

        Instances testData = loadData(TRAIN_DATA_PATH);

        testData = new Instances(testData, 0, Math.min(20000, testData.numAttributes()));
        testData.setClassIndex(testData.numAttributes() - 1);


        model = new MultilayerPerceptron();
        model.setLearningRate(0.025);
        model.setMomentum(0.5);
        model.setTrainingTime(500);
        model.setHiddenLayers("32");

        System.out.println("Beginne Training des Modells");

        try {
            model.buildClassifier(testData);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim trainieren des Modells: " + e);
        } finally {
            System.out.println("Modell erfolgreich trainiert.");
        }

        System.out.println("Evaluiere Modell");
        evaluateModel();

        System.out.println("Speichere Modell");

        try {
            SerializationHelper.write(MODEL_PATH, model);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim speichern des Modells: " + e);
        } finally {
            System.out.println("Modell gespeichert unter: " + MODEL_PATH);
        }
    }

    public void evaluateModel(){

        Instances testData = loadData(TEST_DATA_PATH);

        Evaluation evaluation;
        try {
            evaluation = new Evaluation(testData);
        } catch (Exception e) {
            throw new RuntimeException("Es konnte keine Evaluation erstellt werden: " + e);
        }

        try {
            evaluation.evaluateModel(model, testData);
        } catch (Exception e) {
            throw new RuntimeException("Es konnte keine Evaluation des Modells durchgeführt werden: " + e);
        }

        try {
            System.out.println(evaluation.toSummaryString("\n---- Evaluation der Testdaten ----\n", false));
            System.out.println(evaluation.toClassDetailsString());
            System.out.println(evaluation.toMatrixString());
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Anzeigen der Evaluation: " + e);
        }
    }

    public int predictNumber(double[] pixel) {

        if(pixel.length != 784) throw new IllegalArgumentException("Es müssen genau 784 Pixel übergeben werden.");

        Instance instance = new DenseInstance(785);

        for (int i = 0; i < pixel.length; i++) {
            instance.setValue(i, pixel[i]);
        }

        double prediction;
        try {
            prediction = model.classifyInstance(instance);
        } catch (Exception e) {
            throw new RuntimeException("Bei der Prediction ist ein Fehler aufgetreten: " + e);
        }

        return (int) prediction;
    }

    private void loadModel(){
        System.out.println("Lade Modell " + MODEL_PATH);

        try {
            model = (MultilayerPerceptron) SerializationHelper.read(MODEL_PATH);
        } catch (Exception e) {
            throw new RuntimeException(MODEL_PATH + " konnte nicht gelesen werden.");
        }
    }

    private Instances loadData(String path){
        Instances data;

        System.out.println("Lade Daten " + path);
        try {
            data = ConverterUtils.DataSource.read(path);
            data.setClassIndex(data.numAttributes() - 1);
        } catch (Exception e) {
            throw new RuntimeException(path + " konnte nicht gelesen werden.");
        }

        return data;
    }

}
