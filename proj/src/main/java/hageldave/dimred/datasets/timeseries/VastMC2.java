package hageldave.dimred.datasets.timeseries;

import FileHandler.FileHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VastMC2 {
    private static VastMC2 instance;
    private static final String SRC_URL = "https://www.vacommunity.org/tiki-download_file.php?fileId=576";

    private static final String FILE_NAME_DATASET = "Boonsong Lekagul waterways readings.csv";
    private static final String FILE_NAME_UNIT_MAPPING = "chemical units of measure.csv";

    private final ArrayList<String[]> data = new ArrayList<>();
    private final HashMap<LocalDate, ArrayList<TypeDate2Data>> date2data = new HashMap<>();
    private final HashMap<Integer, ArrayList<TypeLocation2Data>> location2data = new HashMap<>();
    private final HashMap<String, String> measure2unit = new HashMap<>();

    // the order in these arrays also define the used indices
    public final String[] klassLocationNames = new String[]{"Boonsri", "Kannika", "Chai", "Kohsoom", "Somchair", "Sakda", "Busarakhan", "Tansanee", "Achara", "Decha"};
    public final String[] klassMeasureNames;
    // public final LocalDate[] klassDateNames;

    private VastMC2() {
        try (Scanner sc = new Scanner(FileHandler.getFileFromZIP(SRC_URL, "", FILE_NAME_UNIT_MAPPING, StandardCharsets.ISO_8859_1))) {
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.isEmpty()) {
                    continue;
                }
                String[] fields = nextLine.split(",");
                measure2unit.put(fields[0], fields[1]);
            }
            klassMeasureNames = measure2unit.keySet().toArray(new String[0]);
        }

        // directory is "" because files are in root directory
        try (Scanner sc = new Scanner(FileHandler.getFileFromZIP(SRC_URL, "", FILE_NAME_DATASET))) {
            boolean firstLineRead = false;
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.isEmpty() || !firstLineRead) {
                    firstLineRead = true;
                    continue;
                }
                String[] fields = nextLine.split(",");

                // add to data
                data.add(fields);

                // parse date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy").withLocale(Locale.US);
                LocalDate localDate = LocalDate.parse(fields[3], formatter);

                // get indices of data
                int locationIndex = Arrays.asList(klassLocationNames).indexOf(fields[2]);
                int measureIndex = Arrays.asList(klassMeasureNames).indexOf(fields[4]);

                // put stuff into maps
                TypeDate2Data date2Data = new TypeDate2Data(
                        Integer.parseInt(fields[0]),
                        Double.parseDouble(fields[1]),
                        locationIndex,
                        measureIndex
                );
                if (date2data.containsKey(localDate))
                    date2data.get(localDate).add(date2Data);
                else {
                    ArrayList<TypeDate2Data> dateList = new ArrayList<>();
                    dateList.add(date2Data);
                    date2data.put(localDate, dateList);
                }

                TypeLocation2Data location2Data = new TypeLocation2Data(
                        Integer.parseInt(fields[0]),
                        Double.parseDouble(fields[1]),
                        localDate,
                        measureIndex);
                if (location2data.containsKey(locationIndex))
                    location2data.get(locationIndex).add(location2Data);
                else {
                    ArrayList<TypeLocation2Data> locationList = new ArrayList<>();
                    locationList.add(location2Data);
                    location2data.put(locationIndex, locationList);
                }
            }
        }
    }

    public ArrayList<TypeDate2Data> getAllOfDate(LocalDate date) {
        return date2data.get(date);
    }

    public ArrayList<TypeLocation2Data> getAllOfLocation(int location) {
        return location2data.get(location);
    }


    public ArrayList<String[]> getData() {
        return data;
    }

    public String getUnitOfMeasure(String measure) {
        return measure2unit.get(measure);
    }

    public static VastMC2 getInstance() {
        if (instance == null)
            instance = new VastMC2();
        return instance;
    }

    public static class TypeLocation2Data {
        public final int id;
        public final double value;
        public final LocalDate sampleDate;
        public final int measure;

        public TypeLocation2Data(int id, double value, LocalDate sampleDate, int measure) {
            this.id = id;
            this.value = value;
            this.sampleDate = sampleDate;
            this.measure = measure;
        }
    }

    public static class TypeDate2Data {
        public final int id;
        public final double value;
        public final int location;
        public final int measure;

        public TypeDate2Data(int id, double value, int location, int measure) {
            this.id = id;
            this.value = value;
            this.location = location;
            this.measure = measure;
        }
    }
}
