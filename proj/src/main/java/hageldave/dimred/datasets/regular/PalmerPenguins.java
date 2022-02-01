package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.*;
import java.util.stream.IntStream;


public class PalmerPenguins {

    // penguin class
    public enum PClass {
        SPECIES,
        ISLAND,
        SEX
    }

    private static PalmerPenguins instance;
    private static final String SRC_URL = "https://zenodo.org/record/3960218/files/allisonhorst/palmerpenguins-v0.1.0.zip";
    private static final String FILE_DIRECTORY = "allisonhorst-palmerpenguins-e5bfd5f/inst/extdata/";
    private static final String FILE_NAME = "penguins.csv";

    protected static final HashMap<String, Integer> species2Int = new HashMap<>();
    protected static final HashMap<String, Integer> island2Int = new HashMap<>();
    protected static final HashMap<String, Integer> sex2Int = new HashMap<>();

    public final String[] speciesKlassNames = new String[]{"Adelie","Gentoo","Chinstrap"};
    public final String[] islandKlassNames = new String[]{"Torgersen","Biscoe","Dream"};
    public final String[] sexKlassNames = new String[]{"male","female"};

    public final double[][] data;

    public final int[] speciesKlass;
    final int[][] speciesKlass2Indices = new int[3][];

    public final int[] islandKlass;
    final int[][] islandKlass2Indices = new int[3][];

    public final int[] sexKlass;
    final int[][] sexKlass2Indices = new int[2][];


    private PalmerPenguins() {
        species2Int.put("Adelie", 0);
        species2Int.put("Gentoo", 1);
        species2Int.put("Chinstrap", 2);

        island2Int.put("Torgersen", 0);
        island2Int.put("Biscoe", 1);
        island2Int.put("Dream", 2);

        sex2Int.put("male", 0);
        sex2Int.put("female", 1);

        ArrayList<double[]> dataset = new ArrayList<>();
        try (Scanner sc = new Scanner(FileHandler.getFileFromZIP(SRC_URL, FILE_DIRECTORY, FILE_NAME))) {
            boolean firstLineRead = false;
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();

                if(nextLine.isEmpty() || !firstLineRead) {
                    firstLineRead = true;
                    continue;
                }

                String[] fields = nextLine.split(",");
                double[] values = new double[8];

                // skip if values aren't given
                if (List.of(fields).contains("NA"))
                    continue;

                for (int i = 2; i < 6; i++) {
                    values[i] = Double.parseDouble(fields[i]);
                }
                values[7] = Double.parseDouble(fields[7]);

                values[0] = species2Int.get(fields[0]);
                values[1] = island2Int.get(fields[1]);
                values[6] = sex2Int.get(fields[6]);

                dataset.add(values);
            }
        }

        data = dataset.stream().map(v -> Arrays.copyOf(v, 8)).toArray(double[][]::new);
        speciesKlass = dataset.stream().mapToInt(v -> (int)v[0]).toArray();
        islandKlass = dataset.stream().mapToInt(v -> (int)v[1]).toArray();
        sexKlass = dataset.stream().mapToInt(v -> (int)v[6]).toArray();

        for(int t=0; t<3; t++) {
            int t_=t;
            speciesKlass2Indices[t] = IntStream.range(0, speciesKlass.length).filter(i->speciesKlass[i]==t_).toArray();
            islandKlass2Indices[t] = IntStream.range(0, islandKlass.length).filter(i->islandKlass[i]==t_).toArray();
        }
        for(int t=0; t<2; t++) {
            int t_=t;
            sexKlass2Indices[t] = IntStream.range(0, sexKlass.length).filter(i->sexKlass[i]==t_).toArray();
        }
    }

    public double[][] getRawData() {
        return data;
    }

    // return each data array from a penguin/island/sex class
    public double[][] getAllOfClass(PClass pClass, int type) {
        switch (pClass) {
            case SPECIES:
                return Arrays.stream(speciesKlass2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            case ISLAND:
                return Arrays.stream(islandKlass2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            case SEX:
                return Arrays.stream(sexKlass2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            default:
                return null;
        }
    }

    public int getNumClasses(PClass pClass) {
        switch (pClass) {
            case SPECIES:
                return speciesKlass2Indices.length;
            case ISLAND:
                return islandKlass2Indices.length;
            case SEX:
                return sexKlass2Indices.length;
        }
        return -1;
    }

    public static PalmerPenguins getInstance() {
        if (instance == null)
            instance = new PalmerPenguins();
        return instance;
    }
}
