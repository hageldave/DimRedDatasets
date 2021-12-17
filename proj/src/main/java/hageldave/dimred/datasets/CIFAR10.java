package hageldave.dimred.datasets;

import FileHandler.FileHandler;

import javax.imageio.ImageIO;
import java.awt.*;
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

    public static final ArrayList<ArrayList<byte[]>> trainingData = new ArrayList<>(5);
    public static final ArrayList<byte[]> allTrainingData = new ArrayList<>(5*BATCH_SIZE);
    public static final ArrayList<ArrayList<Integer>> trainingDataLabels = new ArrayList<>(5);
    public static final ArrayList<Integer> allTrainingDataLabels = new ArrayList<>(5*BATCH_SIZE);

    public static final ArrayList<byte[]> testData = new ArrayList<>(10000);
    public static final ArrayList<Integer> testDataLabels = new ArrayList<>(10000);

    public final String[] klassNames = new String[]{"airplane","automobile","bird", "cat", "deer", "dog", "frog", "horse", "ship", "truck"};

    final int[][] klass2IndicesTrainingData = new int[10][];
    final int[][] klass2IndicesTestData = new int[10][];

    private CIFAR10() {
        // read images
        try (BufferedReader br = FileHandler.getFile(SRC_URL, FILE_NAME)) {
            for (int j = 1; j < 6; j++) {
                // read all
                ArrayList<byte[]> dataBatch = new ArrayList<>(BATCH_SIZE);
                ArrayList<Integer> dataBatchLabels = new ArrayList<>(BATCH_SIZE);
                try (InputStream cifarIS = FileHandler.getFileFromTar("./datasets/cifar-10-binary.tar.gz", DIRECTORY, "data_batch_" + j + ".bin")) {
                    for (int i = 0; i < BATCH_SIZE; i++) {
                        int label = cifarIS.read();
                        dataBatchLabels.add(label);
                        allTrainingDataLabels.add(label);

                        byte[] b = new byte[3072];
                        cifarIS.read(b);

                        allTrainingData.add(b);
                        dataBatch.add(b);
                    }
                }
                trainingDataLabels.add(dataBatchLabels);
                trainingData.add(dataBatch);
            }

            try (InputStream cifarIS = FileHandler.getFileFromTar("./datasets/cifar-10-binary.tar.gz", "cifar-10-batches-bin/", "test_batch.bin")) {
                for (int i = 0; i < BATCH_SIZE; i++) {
                    testDataLabels.add(cifarIS.read());
                    byte[] b = new byte[3072];
                    cifarIS.read(b);
                    testData.add(b);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int t=0; t < 10; t++) {
            int t_=t;
            klass2IndicesTrainingData[t] = IntStream.range(0, allTrainingDataLabels.size()).filter(i->allTrainingDataLabels.get(i)==t_).toArray();
            klass2IndicesTestData[t] = IntStream.range(0, testDataLabels.size()).filter(i->testDataLabels.get(i)==t_).toArray();
        }
    }

    public byte[][] getAllOfClass(Dataset type, int klass) {
        switch(type) {
            case TRAINING:
                return Arrays.stream(klass2IndicesTrainingData[klass]).mapToObj(i->allTrainingData.toArray()[i]).toArray(byte[][]::new);
            case TEST:
                return Arrays.stream(klass2IndicesTestData[klass]).mapToObj(i->testData.toArray()[i]).toArray(byte[][]::new);
            default:
                return null;
        }
    }

    public static BufferedImage getImageAtIndex(Dataset type, int index) {
        byte[] b = new byte[3072];
        switch(type) {
            case TRAINING:
                b = allTrainingData.get(index);
                break;
            case TEST:
                b = testData.get(index);
                break;
        }

        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                Color color = new Color(
                        b[1 + 1024 * 0 + row * 32 + col - 1] & 0xFF,
                        b[1 + 1024 * 1 + row * 32 + col - 1] & 0xFF,
                        b[1 + 1024 * 2 + row * 32 + col - 1] & 0xFF);
                image.setRGB(col, row, color.getRGB());
            }
        }
        return image;
    }

    public static BufferedImage toImage(final byte[] imageData) {
        BufferedImage image = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < 32; row++) {
            for (int col = 0; col < 32; col++) {
                Color color = new Color(
                        imageData[1 + 1024 * 0 + row * 32 + col - 1] & 0xFF,
                        imageData[1 + 1024 * 1 + row * 32 + col - 1] & 0xFF,
                        imageData[1 + 1024 * 2 + row * 32 + col - 1] & 0xFF);
                image.setRGB(col, row, color.getRGB());
            }
        }
        return image;
    }

    public int getNumClasses() {
        return klassNames.length;
    }

    public static CIFAR10 getInstance() {
        if(instance==null)
            instance = new CIFAR10();
        return instance;
    }

    public static void main(String[] args) throws IOException {
        CIFAR10 ds = CIFAR10.getInstance();
        ImageIO.write(toImage(ds.getAllOfClass(Dataset.TRAINING, 8)[4999]), "jpeg", new FileOutputStream("./out.jpg"));
    }
}
