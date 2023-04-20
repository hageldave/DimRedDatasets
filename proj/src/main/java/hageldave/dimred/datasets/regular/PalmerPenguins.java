package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.*;
import java.util.stream.IntStream;


public class PalmerPenguins {

    // penguin class
    public enum PCategory {
        SPECIES,
        ISLAND,
        SEX,
        YEAR,
    }

    private static PalmerPenguins instance;
    private static final String SRC_URL = "https://zenodo.org/record/3960218/files/allisonhorst/palmerpenguins-v0.1.0.zip";
    private static final String FILE_DIRECTORY = "allisonhorst-palmerpenguins-e5bfd5f/inst/extdata/";
    private static final String FILE_NAME = "penguins.csv";

    protected static final HashMap<Integer, Integer> year2Int = new HashMap<>();
    protected static final HashMap<String, Integer> species2Int = new HashMap<>();
    protected static final HashMap<String, Integer> island2Int = new HashMap<>();
    protected static final HashMap<String, Integer> sex2Int = new HashMap<>();

    public final String[] speciesCategoryNames = new String[]{"Adelie","Gentoo","Chinstrap"};
    public final String[] islandCategoryNames = new String[]{"Torgersen","Biscoe","Dream"};
    public final String[] sexCategoryNames = new String[]{"male","female"};

    public final double[][] data;
    public final String[] dataColumnNames = new String[]{"bill_length_mm", "bill_depth_mm", "flipper_length_mm", "body_mass_g"};

    public final int[] speciesCategory;
    final int[][] speciesCategory2Indices = new int[3][];

    public final int[] islandCategory;
    final int[][] islandCategory2Indices = new int[3][];

    public final int[] sexCategory;
    final int[][] sexCategory2Indices = new int[2][];

    public final int[] year;
    public int[][] year2Indices = new int[3][];

    private PalmerPenguins() {
        species2Int.put("Adelie", 0);
        species2Int.put("Gentoo", 1);
        species2Int.put("Chinstrap", 2);

        island2Int.put("Torgersen", 0);
        island2Int.put("Biscoe", 1);
        island2Int.put("Dream", 2);

        sex2Int.put("male", 0);
        sex2Int.put("female", 1);

        year2Int.put(2007, 0);
        year2Int.put(2008, 1);
        year2Int.put(2009, 2);

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

                values[0] = species2Int.get(fields[0]);
                values[1] = island2Int.get(fields[1]);
                values[6] = sex2Int.get(fields[6]);
                values[7] = year2Int.get(Integer.valueOf(fields[7]));

                dataset.add(values);
            }
        }

        data = dataset.stream().map(v -> Arrays.copyOfRange(v, 2,6)).toArray(double[][]::new);

        speciesCategory = dataset.stream().mapToInt(v -> (int)v[0]).toArray();
        islandCategory = dataset.stream().mapToInt(v -> (int)v[1]).toArray();
        sexCategory = dataset.stream().mapToInt(v -> (int)v[6]).toArray();
        year = dataset.stream().mapToInt(v -> (int)v[7]).toArray();

        for(int t=0; t<3; t++) {
            int t_=t;
            speciesCategory2Indices[t] = IntStream.range(0, speciesCategory.length).filter(i->speciesCategory[i]==t_).toArray();
            islandCategory2Indices[t] = IntStream.range(0, islandCategory.length).filter(i->islandCategory[i]==t_).toArray();
            year2Indices[t] = IntStream.range(0, year.length).filter(i->year[i]==t_).toArray();
        }
        for(int t=0; t<2; t++) {
            int t_=t;
            sexCategory2Indices[t] = IntStream.range(0, sexCategory.length).filter(i->sexCategory[i]==t_).toArray();
        }
    }

    public double[][] getRawData() {
        return data;
    }

    // return each data array from a penguin/island/sex class
    public double[][] getAllOfCategory(PCategory pCategory, int type) {
        switch (pCategory) {
            case SPECIES:
                return Arrays.stream(speciesCategory2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            case ISLAND:
                return Arrays.stream(islandCategory2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            case SEX:
                return Arrays.stream(sexCategory2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
            case YEAR:
                return Arrays.stream(year2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
                default:
                return null;
        }
    }

    public int getNumCategories(PCategory pCategory) {
        switch (pCategory) {
            case SPECIES:
                return speciesCategory2Indices.length;
            case ISLAND:
                return islandCategory2Indices.length;
            case SEX:
                return sexCategory2Indices.length;
            case YEAR:
                return year2Indices.length;
        }
        return -1;
    }

    public static PalmerPenguins getInstance() {
        if (instance == null)
            instance = new PalmerPenguins();
        return instance;
    }
}
