package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class SwissRoll {

    public enum DATA_TYPE {
        PRE,
        ROLL
    }

    private static SwissRoll instance;
    private static final String PRE_SRC_DATA = "https://people.cs.uchicago.edu/~dinoj/manifold/preswissroll.dat";
    private static final String PRE_SRC_LBL = "https://people.cs.uchicago.edu/~dinoj/manifold/preswissroll_labels.dat";
    private static final String ROLL_SRC_DATA = "https://people.cs.uchicago.edu/~dinoj/manifold/swissroll.dat";

    private static final String FILE_NAME_DATA = "preswissroll.dat";
    private static final String FILE_NAME_LBL = "preswissroll_labels.dat";
    private static final String ROLL_FILE_NAME_DATA = "swissroll.dat";

    public final double[][] preData;
    public final double[][] rollData;
    public final int[] preKlass;
    public final int[] rollKlass;
    public final String[] klassNames = new String[]{"1","2","3","4"};
    final int[][] preKlass2Indices = new int[4][];
    final int[][] rollKlass2Indices = new int[4][];

    private SwissRoll() {
        ArrayList<double[]> pre_dataset = new ArrayList<>();
        ArrayList<double[]> roll_dataset = new ArrayList<>();
        try (
                Scanner preDataSC = new Scanner(FileHandler.getFile(PRE_SRC_DATA, FILE_NAME_DATA));
                Scanner preLabelSC = new Scanner(FileHandler.getFile(PRE_SRC_LBL, FILE_NAME_LBL));
                Scanner rollDataSC = new Scanner(FileHandler.getFile(ROLL_SRC_DATA, ROLL_FILE_NAME_DATA))
        ) {
            ArrayList<String> label_fields = new ArrayList<>();
            while (preDataSC.hasNextLine() && preLabelSC.hasNextLine()) {
                String dataNextLine = preDataSC.nextLine();
                String labelNextLine = preLabelSC.nextLine();
                if(dataNextLine.isEmpty() || labelNextLine.isEmpty()){
                    continue;
                }

                String[] dataFields = dataNextLine.split(" ");
                String[] labelFields = labelNextLine.split(" ");

                dataFields = Arrays.stream(dataFields).filter(e->!e.equals("")).toArray(String[]::new);
                labelFields = Arrays.stream(labelFields).filter(e->!e.equals("")).toArray(String[]::new);

                label_fields.add(labelFields[0]);

                double[] values = new double[3];
                values[0] = Double.parseDouble(dataFields[0]);
                values[1] = Double.parseDouble(dataFields[1]);
                values[2] = Double.parseDouble(labelFields[0]);

                pre_dataset.add(values);
            }


            while (rollDataSC.hasNextLine()) {
                String rollNextLine = rollDataSC.nextLine();

                if (rollNextLine.isEmpty()) {
                    continue;
                }
                String[] dataFields = rollNextLine.split(" ");

                dataFields = Arrays.stream(dataFields).filter(e->!e.equals("")).toArray(String[]::new);

                double[] values = new double[4];
                values[0] = Double.parseDouble(dataFields[0]);
                values[1] = Double.parseDouble(dataFields[1]);
                values[2] = Double.parseDouble(dataFields[2]);
                values[3] = Double.parseDouble(label_fields.get(0));
                roll_dataset.add(values);
            }
        }

        rollData = roll_dataset.stream().map(v -> Arrays.copyOf(v, 3)).toArray(double[][]::new);
        preData = pre_dataset.stream().map(v -> Arrays.copyOf(v, 2)).toArray(double[][]::new);
        preKlass = pre_dataset.stream().mapToInt(v -> (int)v[2]).toArray();
        rollKlass = roll_dataset.stream().mapToInt(v -> (int)v[3]).toArray();
        for(int t=0; t<4; t++) {
            int t_=t;
            preKlass2Indices[t] = IntStream.range(0, preKlass.length).filter(i-> preKlass[i]==t_).toArray();
            rollKlass2Indices[t] = IntStream.range(0, rollKlass.length).filter(i-> rollKlass[i]==t_).toArray();
        }
    }

    public double[][] getAllOfClass(DATA_TYPE dataType, int type) {
        if (dataType == DATA_TYPE.ROLL) {
            return Arrays.stream(rollKlass2Indices[type]).mapToObj(i-> rollData[i]).toArray(double[][]::new);
        } else {
            return Arrays.stream(preKlass2Indices[type]).mapToObj(i-> preData[i]).toArray(double[][]::new);
        }
    }

    public double[][] getRollData(){
        return rollData;
    }

    public static SwissRoll getInstance() {
        if(instance==null)
            instance = new SwissRoll();
        return instance;
    }

    public int getNumClasses() {
        return preKlass2Indices.length;
    }
}
