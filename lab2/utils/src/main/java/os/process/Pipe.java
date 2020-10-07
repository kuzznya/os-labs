package os.process;

import os.utils.Loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Pipe implements Closeable {

//    <READ descriptor> <WRITE descriptor>
    private final int[] descriptors = new int[2];
    private State state;

    private PipeInputStream inputStream = null;
    private PipeOutputStream outputStream = null;

    public Pipe() {
        init(descriptors);
        state = State.READ_WRITE;
    }

    public Pipe(int[] descriptors, State state) {
        if (descriptors.length != 2)
            throw new IllegalArgumentException("descriptors count should always be 2");
        this.descriptors[0] = descriptors[0];
        this.descriptors[1] = descriptors[1];
        this.state = state;
    }

    public InputStream getInputStream() {
        if (inputStream == null)
            inputStream = new PipeInputStream();

        return inputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new PipeOutputStream();

        return outputStream;
    }

    @Override
    public void close() {
        closeRead();
        closeWrite();
        state = State.CLOSED;
    }

    public void closeRead() {
        if (state == State.CLOSED || state == State.WRITE)
            return;

        closeReadNative(descriptors[0]);

        if (state == State.READ_WRITE)
            state = State.WRITE;
        else if (state == State.READ)
            state = State.CLOSED;
    }

    public void closeWrite() {
        if (state == State.CLOSED || state == State.READ)
            return;

        closeWriteNative(descriptors[1]);

        if (state == State.READ_WRITE)
            state = State.READ;
        else if (state == State.WRITE)
            state = State.CLOSED;
    }

    private static native void init(int[] descriptors);
    private static native void closeReadNative(int readDescriptor);
    private static native void closeWriteNative(int writeDescriptor);

    private static native int read(int readDescriptor, byte[] buffer);
    private static native int write(int writeDescriptor, byte[] buffer);


    public enum State {
        READ_WRITE,
        READ,
        WRITE,
        CLOSED;

        public boolean readable() {
            return this == State.READ_WRITE || this == State.READ;
        }

        public boolean writable() {
            return this == State.READ_WRITE || this == State.WRITE;
        }
    }

    private class PipeInputStream extends InputStream {

        private static final int BUFFER_SIZE = 1024;
        private byte[] buffer = new byte[BUFFER_SIZE];
        int pos = 0;
        int len = 0;
        boolean wasRead = false;

        public PipeInputStream() {
            Arrays.fill(buffer, (byte) -1);
        }

        @Override
        public int read() throws IOException {
            if (!state.readable())
                throw new IOException("Pipe is not readable");

            if (len == BUFFER_SIZE && pos == BUFFER_SIZE || !wasRead) {
                len = Pipe.read(descriptors[0], buffer);
                pos = 0;
                wasRead = true;
            }

            if (pos > len)
                return -1;
            return buffer[pos++];
        }

        @Override
        public void close() {
            Pipe.this.closeRead();
        }
    }

    private class PipeOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            if (!state.writable())
                throw new IOException("Pipe is not writable");

            byte[] buffer = {(byte) (b & 0xFF)};
            int result = Pipe.write(descriptors[1], buffer);

            if (result < 0)
                throw new IOException("Pipe writing error");
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (!state.writable())
                throw new IOException("Pipe is not writable");

            int result = Pipe.write(descriptors[1], b);

            if (result < 0)
                throw new IOException("Pipe writing error");
        }

        @Override
        public void close() {
            Pipe.this.closeWrite();
        }
    }


    static {
        Loader.loadNativeLibrary();
    }
}
