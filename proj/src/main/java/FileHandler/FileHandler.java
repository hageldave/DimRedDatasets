package FileHandler;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHandler implements RPByteChannelCallback {
    private static final int BUFFER_SIZE = 4096;

    public static BufferedReader getFile(String srcUrl, String fileName) {
        String filePath = getTargetDirectory() + fileName;
        BufferedReader reader;
        new File(getTargetDirectory()).mkdirs();
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
            throw new RuntimeException("Couldn't load file!", e);
        }
        return reader;
    }

    public static BufferedReader getFile(String fileName, RPByteChannel rpByteChannel) {
        String filePath = getTargetDirectory() + fileName;
        BufferedReader reader;
        new File(getTargetDirectory()).mkdirs();
        File file = new File(filePath);

        try {
            if (!file.isFile()) {
                File targetFile = new File(filePath);
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fos.getChannel().transferFrom(rpByteChannel, 0, Long.MAX_VALUE);
                }
            }
            reader = new BufferedReader(new FileReader(filePath));
            rpByteChannel.close();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load file!", e);
        }
        return reader;
    }

    public BufferedReader getFileWithProgress(String srcUrl, String fileName) {
        BufferedReader br;
        try {
            URL url = new URL(srcUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            RPByteChannel rbc = new RPByteChannel(Channels.newChannel(url.openStream()), conn.getContentLength(), this);
            br = getFile(fileName, rbc);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load file!", e);
        }
        return br;
    }
    
    public static BufferedReader getFileFromZIP(String srcUrl, String directory, String fileName, Charset charset) {
        String filePath = getTargetDirectory() + fileName;
        BufferedReader reader;
        try {
            new File(getTargetDirectory()).mkdirs();
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
            reader = new BufferedReader(new FileReader(filePath, charset));
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load file!", e);
        }
        return reader;
    }

    public static BufferedReader getFileFromZIP(String srcUrl, String directory, String fileName) {
        return getFileFromZIP(srcUrl, directory, fileName, StandardCharsets.UTF_8);
    }

    public static InputStream getFileFromGZIP(String srcUrl, String fileName) {
        String filePath = getTargetDirectory() + fileName;
        InputStream targetStream = null;
        new File(getTargetDirectory()).mkdirs();
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
                    while ((read = zis.read(bytesIn)) != -1) {
                        bos.write(bytesIn, 0, read);
                    }
                }
                targetStream = new FileInputStream(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Couldn't load file!", e);
            }
        }
        return targetStream;
    }

    public static InputStream readFileFromTar(String tarPath, String fileDirectory, String fileName) throws IOException {
        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(tarPath)));

        ArchiveEntry entry = tarInput.getNextTarEntry();
        InputStream isr = null;
        while (entry != null) {
            if (entry.getName().equals(fileDirectory + fileName) && !entry.isDirectory()) {
                isr = tarInput; // Read
                break;
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
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    @Override
    public void rpByteChannelCallback(RPByteChannel rpbc, double progress) {
        System.out.printf("Download progress: %d bytes received | Percent: %.02f%%%n", rpbc.getBytesRead(), progress);
    }

    public static String getTargetDirectory() {
        return System.getProperty("dimred.datasets.directorypath", "./datasets/");
    }
}