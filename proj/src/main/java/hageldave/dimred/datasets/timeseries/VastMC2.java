package hageldave.dimred.datasets.timeseries;

import FileHandler.FileHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class VastMC2 {
    private static VastMC2 instance;
    private static final String SRC_URL = "https://www.vacommunity.org/tiki-download_file.php?fileId=576";

    private static final String FILE_NAME_DATASET = "Boonsong Lekagul waterways readings.csv";
    private static final String FILE_NAME_UNIT_MAPPING = "chemical units of measure.csv";

    private final static ArrayList<String[]> data = new ArrayList<>();
    private final static HashMap<LocalDate, ArrayList<String[]>> time2data = new HashMap<>();
    private final static HashMap<String, ArrayList<String[]>> location2data = new HashMap<>();
    private final static HashMap<String, String> measure2unit = new HashMap<>();

    public final String[] klassLocationNames = new String[]{"Boonsri", "Kannika", "Chai", "Kohsoom", "Somchair", "Sakda", "Busarakhan", "Tansanee", "Achara", "Decha"};
    public final String[] klassMeasureNames;

    private VastMC2() {
        // directory is "" because files are in root directory
        try (Scanner sc = new Scanner(FileHandler.getFileFromZIP(SRC_URL, "", FILE_NAME_DATASET))) {
            ArrayList<String> collectMeasures = new ArrayList<>(103);
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

                // fill klassMeasureNames array, so that the developer can see what "measure" classes are available
                if (!collectMeasures.contains(fields[4])) {
                    collectMeasures.add(fields[4]);
                }

                // parse date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yy").withLocale(Locale.US);
                LocalDate dateTime = LocalDate.parse(fields[3], formatter);

                // put stuff into maps
                if (time2data.containsKey(dateTime))
                    time2data.get(dateTime).add(new String[]{fields[0], fields[1], fields[2], fields[3]});
                else {
                    ArrayList<String[]> entriesForDate = new ArrayList<>();
                    entriesForDate.add(new String[]{fields[0], fields[1], fields[2], fields[3]});
                    time2data.put(dateTime, entriesForDate);
                }

                if (location2data.containsKey(fields[2]))
                    location2data.get(fields[2]).add(new String[]{fields[0], fields[1], fields[2], fields[3]});
                else {
                    ArrayList<String[]> entriesForLoc = new ArrayList<>();
                    entriesForLoc.add(new String[]{fields[0], fields[1], fields[3], fields[4]});
                    location2data.put(fields[2], entriesForLoc);
                }
            }
            klassMeasureNames = collectMeasures.toArray(String[]::new);
        }


        try (Scanner sc = new Scanner(FileHandler.getFileFromZIP(SRC_URL, "", FILE_NAME_UNIT_MAPPING, StandardCharsets.ISO_8859_1))) {
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.isEmpty()) {
                    continue;
                }
                String[] fields = nextLine.split(",");
                measure2unit.put(fields[0], fields[1]);
            }
        }
    }

    public static ArrayList<String[]> getAllOfDate(LocalDate date) {
        return time2data.get(date);
    }

    public static ArrayList<String[]> getAllOfLocation(String location) {
        return location2data.get(location);
    }

    public static ArrayList<String[]> getData() {
        return data;
    }

    public static String getUnitOfMeasure(String measure) {
        return measure2unit.get(measure);
    }

    public static VastMC2 getInstance() {
        if (instance == null)
            instance = new VastMC2();
        return instance;
    }

}
