package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class WineQualityDataset {
    private static WineQualityDataset instance;
    private static final String SRC_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/wine-quality/winequality-red.csv";
    private static final String FILE_NAME = "winequality-red.csv";

    public final double[][] data;
    public final int[] category;
    final int[][] category2Indices = new int[10][];

    private WineQualityDataset() {
        ArrayList<double[]> dataset = new ArrayList<>();
        try(Scanner sc = new Scanner(FileHandler.getFile(SRC_URL, FILE_NAME))) {
            // first line consists of attribute description, so we skip that
            boolean firstLineRead = false;
            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.isEmpty() || !firstLineRead) {
                    firstLineRead = true;
                    continue;
                }
                String[] fields = nextLine.split(";");
                double[] values = new double[12];
                for (int i = 0; i < values.length; i++) {
                    values[i] = Double.parseDouble(fields[i]);

                }
                dataset.add(values);
            }
            data = dataset.stream().map(v -> Arrays.copyOf(v, 11)).toArray(double[][]::new);
            category = dataset.stream().mapToInt(v -> (int) v[11]).toArray();
            for (int t = 0; t < 10; t++) {
                int t_ = t;
                category2Indices[t] = IntStream.range(0, category.length).filter(i -> category[i] == t_).toArray();
            }
        }
    }


    public int getAllOfCategory() {
        return category2Indices.length;
    }

    public double[][] getAllOfClass(int type) {
        return Arrays.stream(category2Indices[type]).mapToObj(i -> data[i]).toArray(double[][]::new);
    }

    public static WineQualityDataset getInstance() {
        if (instance == null)
            instance = new WineQualityDataset();
        return instance;
    }
}
