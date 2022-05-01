package hageldave.dimred.datasets.regular;


import FileHandler.FileHandler;

import java.io.*;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

public class MNISTDataset {

	private static MNISTDataset instance;
	private static final String SRC_URL_IMG = "http://yann.lecun.com/exdb/mnist/train-images-idx3-ubyte.gz";
	private static final String SRC_URL_LBL = "http://yann.lecun.com/exdb/mnist/train-labels-idx1-ubyte.gz";
	private static final String GZIP_DIRECTORY = FileHandler.getTargetDirectory();
	private static final String IMG_FILENAME = "train-images-idx3-ubyte.gz";
	private static final String LBL_FILENAME = "train-labels-idx1-ubyte.gz";

	public final double[][] data;
	public final int[] category;
	public final int[][] category2Indices = new int[10][];

	private MNISTDataset() {
		// read images
		File imgFile = new File( GZIP_DIRECTORY + IMG_FILENAME);
		try (
				BufferedReader br = FileHandler.getFile(SRC_URL_IMG, IMG_FILENAME);
				InputStream is = new FileInputStream(imgFile);
				BufferedInputStream bis = new BufferedInputStream(is);
				GZIPInputStream zis = new GZIPInputStream(bis);
				DataInputStream dis = new DataInputStream(zis))
		{
			@SuppressWarnings("unused")
			int magic = dis.readInt();
			int nimages = dis.readInt();
			int nrows = dis.readInt();
			int ncols = dis.readInt();

			double[][] data = new double[nimages][nrows*ncols];
			final double toUnit = 1.0/255.0;
			for(int i=0; i<nimages; i++) {
				for(int j=0; j<nrows*ncols; j++) {
					int pixel = dis.read();
					data[i][j] = pixel*toUnit;
				}
			}
			this.data = data;
		} catch (IOException e) {
			throw new RuntimeException("could not load data from file",e);
		}

		// read labels
		File lblFile = new File( GZIP_DIRECTORY + LBL_FILENAME);
		try (
				BufferedReader br = FileHandler.getFile(SRC_URL_LBL, LBL_FILENAME);
				InputStream is = new FileInputStream(lblFile);
				BufferedInputStream bis = new BufferedInputStream(is);
				GZIPInputStream zis = new GZIPInputStream(bis);
				DataInputStream dis = new DataInputStream(zis))
		{
			@SuppressWarnings("unused")
			int magic = dis.readInt();
			int nlabels = dis.readInt();

			int[] labels = new int[nlabels];
			for(int i=0; i<nlabels; i++) {
				labels[i] = dis.readUnsignedByte();
			}
			this.category = labels;
		} catch (IOException e) {
			throw new RuntimeException("could not load data from file",e);
		}
		// get indices per class (digit)
		for(int t=0; t<10; t++) {
			int t_=t;
			category2Indices[t] = IntStream.range(0, category.length).filter(i-> category[i]==t_).toArray();
		}
	}
	
	public double[][] getAllOfCategory(int type){
		return Arrays.stream(category2Indices[type]).mapToObj(i->data[i]).toArray(double[][]::new);
	}

	public int getNumCategories() {
		return category2Indices.length;
	}

	public static MNISTDataset getInstance() {
		if(instance==null)
			instance = new MNISTDataset();
		return instance;
	}
}
