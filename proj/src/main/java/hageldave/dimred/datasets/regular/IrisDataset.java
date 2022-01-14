package hageldave.dimred.datasets.regular;

import FileHandler.FileHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;


public class IrisDataset {
	
	private static IrisDataset instance;
	private static final String SRC_URL = "https://archive.ics.uci.edu/ml/machine-learning-databases/iris/iris.data";
	private static final String FILE_NAME = "iris.data";
	public final double[][] data;
	public final int[] klass;
	public final String[] klassNames = new String[]{"setosa","versicolor","virginica"};
	final int[][] klass2Indices = new int[3][];
	
	private IrisDataset() {
		ArrayList<double[]> dataset = new ArrayList<>();
		try (Scanner sc = new Scanner(FileHandler.getFile(SRC_URL, FILE_NAME))) {
			while(sc.hasNextLine()){
				String nextLine = sc.nextLine();
				if(nextLine.isEmpty()){
					continue;
				}
				String[] fields = nextLine.split(",");
				double[] values = new double[5];
				values[0] = Double.parseDouble(fields[0]);
				values[1] = Double.parseDouble(fields[1]);
				values[2] = Double.parseDouble(fields[2]);
				values[3] = Double.parseDouble(fields[3]);
				if(fields[4].contains("setosa")){
					values[4] = 0; // setosa class
				} else if(fields[4].contains("versicolor")) {
					values[4] = 1; // versicolor class
				} else {
					values[4] = 2; // virginica class
				}
				dataset.add(values);
			}
		}
		
		data = dataset.stream().map(v -> Arrays.copyOf(v, 4)).toArray(double[][]::new);
		klass = dataset.stream().mapToInt(v -> (int)v[4]).toArray();
		for(int t=0; t<3; t++) {
			int t_=t;
			klass2Indices[t] = IntStream.range(0, klass.length).filter(i->klass[i]==t_).toArray();
		}
	}
	
	public double[][] getAllOfClass(int type){
		return Arrays.stream(klass2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
	}
	
	public static IrisDataset getInstance() {
		if(instance==null)
			instance = new IrisDataset();
		return instance;
	}
	
	public int getNumClasses() {
		return klass2Indices.length;
	}
}

