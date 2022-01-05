package FileHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Wrapper class for RBC that provides access to progress of reading the source
 */
public class RPByteChannel implements ReadableByteChannel {
    private final RPByteChannelCallback callback;
    private final long expectedSize;
    private final ReadableByteChannel rbc;
    private long bytesRead;
    private double roundedProgress = 0.0;

    public RPByteChannel(ReadableByteChannel rbc, long expectedSize, RPByteChannelCallback callback) {
        this.callback = callback;
        this.expectedSize = expectedSize;
        this.rbc = rbc;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int n;
        double progress;

        if ((n = rbc.read(dst)) > 0) {
            bytesRead += n;
            progress = expectedSize > 0 ? (double) bytesRead / (double) expectedSize * 100.0 : -1.0;
            if (roundedProgress != Math.floor(progress)) {
                callback.rpByteChannelCallback(this, progress);
                roundedProgress = Math.floor(progress);
            }
        }
        return n;
    }

    @Override
    public boolean isOpen() {
        return rbc.isOpen();
    }

    @Override
    public void close() throws IOException {
        rbc.close();
    }

    public long getBytesRead() {
        return bytesRead;
    }
}
