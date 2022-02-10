package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.Arrays;
import java.util.LinkedList;
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

    public final AutoData[] data;

    public final int[][] cylinders2Indices = new int[4][];
    public final int[][] modelyear2Indices = new int[13][];
    public final int[][] origin2Indices = new int[3][];

    private AutoMPG() {
        LinkedList<AutoData> allEntries = new LinkedList<>();
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

                AutoData entry = new AutoData(
                        Double.parseDouble(fields[0]),
                        Integer.parseInt(fields[1]),
                        Double.parseDouble(fields[2]),
                        horsepower,
                        Double.parseDouble(fields[4]),
                        Double.parseDouble(fields[5]),
                        Integer.parseInt(fields[6]),
                        Integer.parseInt(fields[7]),
                        fields[8]
                );
                allEntries.add(entry);
            }
            data = allEntries.toArray(new AutoData[0]);


            int[] cylinderKlasses = allEntries.stream().mapToInt(v -> v.cylinders).toArray();
            int[] modelyearKlasses = allEntries.stream().mapToInt(v -> v.modelYear).toArray();
            int[] originKlasses = allEntries.stream().mapToInt(v -> v.origin).toArray();

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

    public AutoData[] getAllOfClass(Property property, int value) {
        switch (property) {
            case CYLINDERS:
                return Arrays.stream(cylinders2Indices[value]).mapToObj(i -> data[i]).toArray(AutoData[]::new);
            case MODEL_YEAR:
                return Arrays.stream(modelyear2Indices[value]).mapToObj(i -> data[i]).toArray(AutoData[]::new);
            case ORIGIN:
                return Arrays.stream(origin2Indices[value]).mapToObj(i -> data[i]).toArray(AutoData[]::new);
            default:
                return new AutoData[]{};
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

    public static class AutoData {
        public final double mpg;
        public final int cylinders;
        public final double displacement;
        public final double horsepower;
        public final double weight;
        public final double acceleration;
        public final int modelYear;
        public final int origin;
        public final String carName;

        public AutoData(double mpg, int cylinders, double displacement, double horsepower,
                        double weight, double acceleration, int modelYear, int origin, String carName) {
            this.mpg = mpg;
            this.cylinders = cylinders;
            this.displacement = displacement;
            this.horsepower = horsepower;
            this.weight = weight;
            this.acceleration = acceleration;
            this.modelYear = modelYear;
            this.origin = origin;
            this.carName = carName;
        }
    }
}
