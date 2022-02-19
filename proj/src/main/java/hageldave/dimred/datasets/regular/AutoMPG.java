package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

public class AutoMPG {
    public enum Property {
        CYLINDERS,
        MODEL_YEAR,
        ORIGIN
    }

    private static AutoMPG instance;
    private static final String SRC_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/auto-mpg/auto-mpg.data";
    private static final String FILE_NAME = "auto-mpg.data";

    public final double[][] data;
    public final String[] label;

    public final int[][] cylinders2Indices = new int[4][];
    public final int[][] modelyear2Indices = new int[13][];
    public final int[][] origin2Indices = new int[3][];

    private AutoMPG() {
        ArrayList<double[]> dataset = new ArrayList<>();
        ArrayList<String> datasetLabels = new ArrayList<>();
        try (Scanner sc = new Scanner(FileHandler.getFile(SRC_URL, FILE_NAME))) {
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if(nextLine.isEmpty()){
                    continue;
                }
                String[] fields = nextLine.replaceAll("\\s\\p{Zs}+", "  ").split("(\\s\\s)|(\")");
                Arrays.stream(fields).map(String::trim).toArray(unused -> fields);

                // there are some values "empty" so we have to find a default value for these cases
                double horsepower = -1;
                if (!Objects.equals(fields[3], "?")) {
                    horsepower = Double.parseDouble(fields[3]);
                }

                double[] values = new double[8];

                values[0] = Double.parseDouble(fields[0]);
                values[1] = Double.parseDouble(fields[1]);
                values[2] = Double.parseDouble(fields[2]);
                values[3] = horsepower;
                values[4] = Double.parseDouble(fields[4]);
                values[5] = Double.parseDouble(fields[5]);
                values[6] = Double.parseDouble(fields[6]);
                values[7] = Double.parseDouble(fields[7]);

                dataset.add(values);
                datasetLabels.add(fields[8]);
            }
            data = dataset.stream().map(v -> Arrays.copyOf(v, 8)).toArray(double[][]::new);
            label = datasetLabels.toArray(String[]::new);

            int[] cylinderKlasses = dataset.stream().mapToInt(v -> (int) v[1]).toArray();
            int[] modelyearKlasses = dataset.stream().mapToInt(v -> (int) v[6]).toArray();
            int[] originKlasses = dataset.stream().mapToInt(v -> (int) v[7]).toArray();

            int[] klasses = new int[]{3, 4, 6, 8};
            for(int t=0; t<4; t++) {
                int t_=klasses[t];
                cylinders2Indices[t] = IntStream.range(0, cylinderKlasses.length).filter(i->cylinderKlasses[i]==t_).toArray();
            }

            klasses = IntStream.range(70, 83).toArray();
            for(int t=0; t<13; t++) {
                int t_=klasses[t];
                modelyear2Indices[t] = IntStream.range(0, modelyearKlasses.length).filter(i->modelyearKlasses[i]==t_).toArray();
            }

            klasses = new int[]{1,2,3};
            for(int t=0; t<3; t++) {
                int t_=klasses[t];
                origin2Indices[t] = IntStream.range(0, originKlasses.length).filter(i->originKlasses[i]==t_).toArray();
            }
        }
    }

    public double[][] getAllOfClass(Property property, int value) {
        switch (property) {
            case CYLINDERS:
                return Arrays.stream(cylinders2Indices[value]).mapToObj(i -> data[i]).toArray(double[][]::new);
            case MODEL_YEAR:
                return Arrays.stream(modelyear2Indices[value]).mapToObj(i -> data[i]).toArray(double[][]::new);
            case ORIGIN:
                return Arrays.stream(origin2Indices[value]).mapToObj(i -> data[i]).toArray(double[][]::new);
            default:
                return new double[][]{};
        }
    }

    public int getNumClasses(Property property) {
        switch (property) {
            case CYLINDERS:
                return cylinders2Indices.length;
            case MODEL_YEAR:
                return modelyear2Indices.length;
            case ORIGIN:
                return origin2Indices.length;
            default:
                return -1;
        }
    }

    public static AutoMPG getInstance() {
        if(instance==null)
            instance = new AutoMPG();
        return instance;
    }
}
