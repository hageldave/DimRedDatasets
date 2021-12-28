package hageldave.dimred.datasets;

import FileHandler.FileHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class CIFAR10 {

    // trainingset class
    public enum Dataset {
        TRAINING,
        TEST
    }

    private static CIFAR10 instance;
    private static final String SRC_URL = "https://www.cs.toronto.edu/~kriz/cifar-10-binary.tar.gz";
    private static final String DIRECTORY = "cifar-10-batches-bin/";
    private static final String FILE_NAME = "cifar-10-binary.tar.gz";

    private static final int BATCH_SIZE = 10000;
    private static final int BATCH_COUNT = 5;

    public static final ArrayList<byte[]> cmbndTrainingData = new ArrayList<>(BATCH_COUNT * BATCH_SIZE);
    public static final ArrayList<double[]> cmbndTrainingDataAsDouble = new ArrayList<>(BATCH_COUNT * BATCH_SIZE);
    public static final ArrayList<Integer> cmbndTrainingDataLbls = new ArrayList<>(BATCH_COUNT * BATCH_SIZE);

    public static final ArrayList<byte[]> testData = new ArrayList<>(BATCH_SIZE);
    public static final ArrayList<double[]> testDataAsDouble = new ArrayList<>(BATCH_SIZE);
    public static final ArrayList<Integer> testDataLbls = new ArrayList<>(BATCH_SIZE);

    public final String[] klassNames = new String[]{"airplane", "automobile", "bird", "cat", "deer", "dog", "frog", "horse", "ship", "truck"};

    final int[][] klass2IndicesTrainingData = new int[10][];
    final int[][] klass2IndicesTestData = new int[10][];

    private CIFAR10() {
        // read images
        FileHandler handler = new FileHandler();
        try (BufferedReader br = handler.getFileWithProgress(SRC_URL, FILE_NAME)) {
            for (int j = 1; j < BATCH_COUNT + 1; j++) {
                // read all
                try (InputStream cifarIS = FileHandler.getFileFromTar("./datasets/" + FILE_NAME, DIRECTORY, "data_batch_" + j + ".bin")) {
                    for (int i = 0; i < BATCH_SIZE; i++) {
                        int label = cifarIS.read();
                        cmbndTrainingDataLbls.add(label);

                        byte[] byteBuf = new byte[3072];
                        cifarIS.read(byteBuf);
                        cmbndTrainingData.add(byteBuf);

                        double[] doubleBuf = new double[3072];
                        for (int t = 0; t < byteBuf.length; t++) {
                            doubleBuf[t] = (byteBuf[t] & 0xFF) / 255.0;
                        }
                        double max = Arrays.stream(doubleBuf).parallel().max().orElse(255);
                        doubleBuf = Arrays.stream(doubleBuf).parallel().map(e -> e / max).toArray();

                        cmbndTrainingDataAsDouble.add(doubleBuf);
                    }
                }
            }

            try (InputStream cifarIS = FileHandler.getFileFromTar("./datasets/" + FILE_NAME, DIRECTORY, "test_batch.bin")) {
                for (int i = 0; i < BATCH_SIZE; i++) {
                    testDataLbls.add(cifarIS.read());
                    byte[] byteBuf = new byte[3072];
                    cifarIS.read(byteBuf);
                    testData.add(byteBuf);

                    double[] doubleBuf = new double[3072];
                    for (int t = 0; t < byteBuf.length; t++) {
                        doubleBuf[t] = (byteBuf[t] & 0xFF) / 255.0;
                    }
                    double max = Arrays.stream(doubleBuf).parallel().max().orElse(255);
                    doubleBuf = Arrays.stream(doubleBuf).parallel().map(e -> e / max).toArray();

                    testDataAsDouble.add(doubleBuf);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int t = 0; t < 10; t++) {
            int t_ = t;
            klass2IndicesTrainingData[t] = IntStream.range(0, cmbndTrainingDataLbls.size()).filter(i -> cmbndTrainingDataLbls.get(i) == t_).toArray();
            klass2IndicesTestData[t] = IntStream.range(0, testDataLbls.size()).filter(i -> testDataLbls.get(i) == t_).toArray();
        }
    }

    public double[][] getAllOfClass(Dataset type, int klass) {
        switch (type) {
            case TRAINING:
                return Arrays.stream(klass2IndicesTrainingData[klass]).mapToObj(i -> cmbndTrainingDataAsDouble.toArray()[i]).toArray(double[][]::new);
            case TEST:
                return Arrays.stream(klass2IndicesTestData[klass]).mapToObj(i -> testDataAsDouble.toArray()[i]).toArray(double[][]::new);
            default:
                return null;
        }
    }

    public static BufferedImage getImageAtIndex(Dataset type, int index) {
        double[] b = new double[3072];
        switch (type) {
            case TRAINING:
                b = cmbndTrainingDataAsDouble.get(index);
                break;
            case TEST:
                b = testDataAsDouble.get(index);
                break;
        }
        return toImage(b);
    }

    public static byte[][] getColorChannels(final byte[] imageData) {
        byte[][] colorChannel = new byte[3][32 * 32];
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                colorChannel[0][row + 32 * col] = imageData[row * 32 + col];
                colorChannel[1][row + 32 * col] = imageData[1024 + row * 32 + col];
                colorChannel[2][row + 32 * col] = imageData[1024 * 2 + row * 32 + col];
            }
        }
        return colorChannel;
    }

    public static double[] getRedChannel(final double[] imageData) {
        double[] redChannel = new double[32 * 32];
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                redChannel[row + 32 * col] = imageData[row * 32 + col];
            }
        }
        return redChannel;
    }

    public static double[] getGreenChannel(final double[] imageData) {
        double[] greenChannel = new double[32 * 32];
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                greenChannel[row + 32 * col] = imageData[1024 + row * 32 + col];
            }
        }
        return greenChannel;
    }

    public static double[] getBlueChannel(final double[] imageData) {
        double[] blueChannel = new double[32 * 32];
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                blueChannel[row + 32 * col] = imageData[1024 * 2 + row * 32 + col];
            }
        }
        return blueChannel;
    }

    public static BufferedImage toImage(final double[] imageData) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);

        double max = Arrays.stream(imageData).parallel().max().orElse(1);

        int multiplier = (int) (255 * max);

        int[] imageDataAsInt = Arrays.stream(imageData).parallel().mapToInt(e -> (int) (e * multiplier)).toArray();
        int[] rgbData = new int[1024];

        for (int i = 0; i < rgbData.length; i++) {
            rgbData[i] = (imageDataAsInt[i] << 16 | imageDataAsInt[i + 1024] << 8 | imageDataAsInt[i + 1024 * 2]);
        }


        image.setRGB(0, 0, 32, 32, rgbData, 0, 32);
        return image;
    }

    public int getNumClasses() {
        return klassNames.length;
    }

    public static CIFAR10 getInstance() {
        if (instance == null)
            instance = new CIFAR10();
        return instance;
    }

    public static void main(String[] args) throws IOException {
        CIFAR10 ds = CIFAR10.getInstance();
        // System.out.println(Arrays.toString(testDataAsDouble.get(0)));
        ImageIO.write(toImage(ds.getAllOfClass(Dataset.TRAINING, 8)[4961]), "jpeg", new FileOutputStream("./out.jpg"));
    }
}
