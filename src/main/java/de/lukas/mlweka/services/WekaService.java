package de.lukas.mlweka.services;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
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
    public static final String HEADER_PATH = BASE_PATH + "header.model";

    private RandomForest model = new RandomForest();
    private Instances header;

    public WekaService() {

        File modelFile = new File(MODEL_PATH);
        if(modelFile.exists() && !modelFile.isDirectory()) {
            loadModel();
        } else {
            trainModel();
        }
        evaluateModel();

    }

    public void trainModel(){

        Instances trainData = loadData(TRAIN_DATA_PATH);
        header = new Instances(trainData, 0);

/*        trainData = new Instances(trainData, 0, Math.min(30000, trainData.numInstances()));
        trainData.setClassIndex(trainData.numAttributes() - 1);*/

        System.out.println("Beginne Training des Modells");

        try {
            model.buildClassifier(trainData);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim trainieren des Modells: " + e);
        } finally {
            System.out.println("Modell erfolgreich trainiert.");
        }

        saveModel();

    }

    public void evaluateModel(){

        Instances testData = loadData(TEST_DATA_PATH);

        try {
            Evaluation evaluation = new Evaluation(testData);
            evaluation.evaluateModel(model, testData);
            System.out.println(evaluation.toSummaryString("\n=== Evaluation der Testdaten ===\n", false));
            System.out.println(evaluation.toClassDetailsString());
            System.out.println(evaluation.toMatrixString());
        } catch (Exception e) {
            throw new RuntimeException("Es konnte keine Evaluation erstellt werden: " + e);
        }

    }

    public int predictNumber(double[] pixel) {

        Instance instance = new DenseInstance(header.numAttributes());
        instance.setDataset(header);

        for (int i = 0; i < pixel.length; i++) {
            instance.setValue(i, pixel[i]);
        }

        instance.setMissing(header.classIndex());

        try {
            int prediction = (int) model.classifyInstance(instance);
            plotDistribution(instance, prediction);
            return prediction;
        } catch (Exception e) {
            throw new RuntimeException("Bei der Prediction ist ein Fehler aufgetreten: " + e);
        }

    }

    private void loadModel(){

        try {
            model = (RandomForest) SerializationHelper.read(MODEL_PATH);
            header = (Instances) SerializationHelper.read(HEADER_PATH);
        } catch (Exception e) {
            throw new RuntimeException(MODEL_PATH + " konnte nicht gelesen werden.");
        } finally {
            System.out.println("Modell geladen: " + MODEL_PATH);
        }

    }

    private void saveModel() {

        try {
            SerializationHelper.write(MODEL_PATH, model);
            SerializationHelper.write(HEADER_PATH, header);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim speichern des Modells: " + e);
        } finally {
            System.out.println("Modell gespeichert unter: " + MODEL_PATH);
        }

    }

    private Instances loadData(String path){

        try {
            Instances data = ConverterUtils.DataSource.read(path);
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(path + " konnte nicht gelesen werden.");
        } finally {
            System.out.println("Daten geladen: " + path);
        }

    }

    private void plotDistribution(Instance instance, int prediction) throws Exception {

        double[] distribution = model.distributionForInstance(instance);

        System.out.println("\n=== Verteilung für diese Instanz ===\n");

        for (int i = 0; i < distribution.length; i++) {
            String className = instance.classAttribute().value(i);

            double percent = distribution[i] * 100;
            int barLength = (int) (percent / 2);
            String bar = "█".repeat(barLength);

            String marker = (i == prediction ? ">" : " ");

            System.out.printf("%s %-2s: %6.2f%% | %s\n", marker, className, percent, bar);
        }

    }



}
