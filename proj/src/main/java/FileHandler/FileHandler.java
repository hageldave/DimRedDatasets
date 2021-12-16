package FileHandler;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHandler {
    private static final String DIRECTORY = "datasets";
    private static final int BUFFER_SIZE = 4096;

    public static BufferedReader getFile(String srcUrl, String fileName) {
        String filePath = "./" + DIRECTORY + "/" + fileName;
        BufferedReader reader;

        new File(DIRECTORY).mkdirs();
        File file = new File(filePath);

        try (ReadableByteChannel rbc = Channels.newChannel(new URL(srcUrl).openStream())) {
            // do something
            if (!file.isFile()) {
                File targetFile = new File(filePath);
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not load file!", e);
        }
        return reader;
    }

    public static BufferedReader getFileFromZIP(String srcUrl, String directory, String fileName) {
        String filePath = "./" + DIRECTORY + "/" + fileName;
        BufferedReader reader;
        try {
            new File(DIRECTORY).mkdirs();
            File file = new File(filePath);

            if (!file.isFile()) {
                try (InputStream is = new URL(srcUrl).openStream();
                     ZipInputStream zipIn = new ZipInputStream(is)) {
                    ZipEntry entry = zipIn.getNextEntry();
                    // iterates over entries in the zip file
                    while (entry != null) {
                        if (entry.getName().equals(directory + fileName) && !entry.isDirectory()) {
                            // if the entry is a file, extracts it
                            extractFile(zipIn, filePath);
                        }
                        zipIn.closeEntry();
                        entry = zipIn.getNextEntry();
                    }
                }
            }
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not load file!", e);
        }
        return reader;
    }

    public static InputStream getFileFromGZIP(String srcUrl, String fileName) {
        String filePath = "./" + DIRECTORY + "/" + fileName;
        InputStream targetStream = null;
        new File(DIRECTORY).mkdirs();
        File file = new File(filePath);
        if (!file.isFile()) {
            try (
                    InputStream is = new URL(srcUrl).openStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    GZIPInputStream zis = new GZIPInputStream(bis);
            ) {
                try (BufferedOutputStream bos =
                             new BufferedOutputStream(new FileOutputStream(filePath))) {
                    byte[] bytesIn = new byte[BUFFER_SIZE];
                    int read;
                    while (( read = zis.read(bytesIn) ) != -1) {
                        bos.write(bytesIn, 0, read);
                    }
                }
                targetStream = new FileInputStream(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Could not load file!", e);
            }
        }
        return targetStream;
    }

    public static InputStreamReader getFileFromTar(String srcUrl, String directory, String fileName) throws IOException {
        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(srcUrl)));

        ArchiveEntry entry = tarInput.getNextTarEntry();
        InputStreamReader isr = null;
        while (entry != null) {
            if (entry.getName().equals(directory + fileName) && !entry.isDirectory()) {
                isr = new InputStreamReader(tarInput); // Read
            }
            entry = tarInput.getNextTarEntry();
        }
        return isr;
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while (( read = zipIn.read(bytesIn) ) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
