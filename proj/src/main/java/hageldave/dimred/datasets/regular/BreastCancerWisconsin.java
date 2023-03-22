package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class BreastCancerWisconsin {
    public static class WisconsinBreastCancerDatabase {
        private static WisconsinBreastCancerDatabase instance;
        private static final String SRC_URL_FILE = "https://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/breast-cancer-wisconsin.data";
        private static final String FILE_NAME_FILE = "breast-cancer-wisconsin.data";
        public final int[][] data;
        public final int[] category;
        public final String[] categoryNames = new String[]{"Benign", "Malignant"};
        final int[][] category2Indices = new int[2][];

        private WisconsinBreastCancerDatabase() {
            ArrayList<int[]> dataset = new ArrayList<>();
            try (Scanner sc = new Scanner(FileHandler.getFile(SRC_URL_FILE, FILE_NAME_FILE))) {
                while(sc.hasNextLine()){
                    String nextLine = sc.nextLine();
                    if(nextLine.isEmpty() || nextLine.contains("?")){
                        continue;
                    }
                    String[] fields = nextLine.split(",");
                    int[] values = new int[11];
                    for (int i = 0; i < 11; i++) {
                        values[i] = Integer.parseInt(fields[i]);
                    }
                    dataset.add(values);
                }
            }
            data = dataset.stream().map(v -> Arrays.copyOf(v, 11)).toArray(int[][]::new);
            category = dataset.stream().mapToInt(v -> v[10]).toArray();
            for(int t=1; t<3; t++) {
                int t_=t*2;
                category2Indices[t-1] = IntStream.range(0, category.length).filter(i-> category[i]==t_).toArray();
            }
        }

        public int[][] getAllOfCategory(int type){
            return Arrays.stream(category2Indices[type]).mapToObj(i->data[i]).toArray(int[][]::new);
        }

        public static WisconsinBreastCancerDatabase getInstance() {
            if(instance==null)
                instance = new WisconsinBreastCancerDatabase();
            return instance;
        }
    }

    public static class WisconsinDiagnosticBreastCancer {
        private static WisconsinDiagnosticBreastCancer instance;
        private static final String SRC_URL_FILE = "https://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/wdbc.data";
        private static final String FILE_NAME_FILE = "wdbc.data";
        public final double[][] data;
        public final int[] category;
        public final String[] categoryNames = new String[]{"Benign", "Malignant"};
        final int[][] category2Indices = new int[2][];

        private WisconsinDiagnosticBreastCancer() {
            ArrayList<double[]> dataset = new ArrayList<>();
            try (Scanner sc = new Scanner(FileHandler.getFile(SRC_URL_FILE, FILE_NAME_FILE))) {
                while(sc.hasNextLine()){
                    String nextLine = sc.nextLine();
                    if(nextLine.isEmpty() || nextLine.contains("?")){
                        continue;
                    }
                    String[] fields = nextLine.split(",");
                    double[] values = new double[32];

                    values[0] = Integer.parseInt(fields[0]);
                    if (fields[1].equals("B")) {
                        values[1] = 0;
                    } else if (fields[1].equals("M")) {
                        values[1] = 1;
                    }
                    for (int i = 2; i < 32; i++) {
                        values[i] = Double.parseDouble(fields[i]);
                    }
                    dataset.add(values);
                }
            }
            data = dataset.stream().map(v -> Arrays.copyOf(v, 32)).toArray(double[][]::new);
            category = dataset.stream().mapToInt(v -> (int) v[1]).toArray();
            for(int t=0; t<2; t++) {
                int t_=t;
                category2Indices[t] = IntStream.range(0, category.length).filter(i-> category[i]==t_).toArray();
            }
        }

        public double[][] getAllOfCategory(int type){
            return Arrays.stream(category2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
        }

        public static WisconsinDiagnosticBreastCancer getInstance() {
            if(instance==null)
                instance = new WisconsinDiagnosticBreastCancer();
            return instance;
        }
    }

    public static class WisconsinPrognosticBreastCancer {
        private static WisconsinPrognosticBreastCancer instance;
        private static final String SRC_URL_FILE = "https://archive.ics.uci.edu/ml/machine-learning-databases/breast-cancer-wisconsin/wpbc.data";
        private static final String FILE_NAME_FILE = "wpbc.data";
        public final double[][] data;
        public final int[] category;
        public final String[] categoryNames = new String[]{"Recur", "Nonrecur"};
        final int[][] category2Indices = new int[2][];

        private WisconsinPrognosticBreastCancer() {
            ArrayList<double[]> dataset = new ArrayList<>();
            try (Scanner sc = new Scanner(FileHandler.getFile(SRC_URL_FILE, FILE_NAME_FILE))) {
                while(sc.hasNextLine()){
                    String nextLine = sc.nextLine();
                    if(nextLine.isEmpty() || nextLine.contains("?")){
                        continue;
                    }
                    String[] fields = nextLine.split(",");
                    double[] values = new double[35];

                    values[0] = Integer.parseInt(fields[0]);
                    if (fields[1].equals("R")) {
                        values[1] = 0;
                    } else if (fields[1].equals("N")) {
                        values[1] = 1;
                    }
                    for (int i = 2; i < 35; i++) {
                        values[i] = Double.parseDouble(fields[i]);
                    }
                    dataset.add(values);
                }
            }
            data = dataset.stream().map(v -> Arrays.copyOf(v, 34)).toArray(double[][]::new);
            category = dataset.stream().mapToInt(v -> (int) v[1]).toArray();
            for(int t=0; t<2; t++) {
                int t_=t;
                category2Indices[t] = IntStream.range(0, category.length).filter(i-> category[i]==t_).toArray();
            }
        }

        public double[][] getAllOfCategory(int type){
            return Arrays.stream(category2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
        }

        public static WisconsinPrognosticBreastCancer getInstance() {
            if(instance==null)
                instance = new WisconsinPrognosticBreastCancer();
            return instance;
        }
    }
}
