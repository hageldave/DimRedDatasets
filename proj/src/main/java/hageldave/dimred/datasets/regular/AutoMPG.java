package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;

public class AutoMPG {
    private static AutoMPG instance;
    private static final String SRC_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/auto-mpg/auto-mpg.data";
    private static final String FILE_NAME = "auto-mpg.data";

    public final double[][] data;
    public final String[] label;

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

                if (Objects.equals(fields[3], "?")) {
                    continue;
                }
                double horsepower = Double.parseDouble(fields[3]);

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

            int[] originCategories = dataset.stream().mapToInt(v -> (int) v[7]).toArray();

            int[] categories = new int[]{1,2,3};
            for(int t=0; t<3; t++) {
                int t_=categories[t];
                origin2Indices[t] = IntStream.range(0, originCategories.length).filter(i->originCategories[i]==t_).toArray();
            }
        }
    }

    public double[][] getAllOfClass(int value) {
        return Arrays.stream(origin2Indices[value]).mapToObj(i -> data[i]).toArray(double[][]::new);
    }

    public int getNumClasses() {
        return origin2Indices.length;
    }

    public static AutoMPG getInstance() {
        if(instance==null)
            instance = new AutoMPG();
        return instance;
    }

    public static void main(String[] args) {
        AutoMPG mpg = getInstance();
        System.out.println(Arrays.deepToString(mpg.data));
    }
}
