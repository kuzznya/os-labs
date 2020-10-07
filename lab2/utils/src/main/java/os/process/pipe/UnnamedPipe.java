package os.process.pipe;

import os.utils.Loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class UnnamedPipe implements ReadPipe, WritePipe {

//    <READ descriptor> <WRITE descriptor>
    private final int[] descriptors = new int[2];
    private PipeState state;

    private PipeInputStream inputStream = null;
    private PipeOutputStream outputStream = null;

    public UnnamedPipe() {
        init(descriptors);
        state = PipeState.READ_WRITE;
    }

    public UnnamedPipe(int[] descriptors, PipeState state) {
        if (descriptors.length != 2)
            throw new IllegalArgumentException("descriptors count should always be 2");
        this.descriptors[0] = descriptors[0];
        this.descriptors[1] = descriptors[1];
        this.state = state;
    }

    @Override
    public PipeState getState() {
        return state;
    }

    @Override
    public InputStream getInputStream() {
        if (inputStream == null)
            inputStream = new PipeInputStream();

        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new PipeOutputStream();

        return outputStream;
    }

    @Override
    public void close() {
        closeRead();
        closeWrite();
        state = PipeState.CLOSED;
    }

    @Override
    public void closeRead() {
        if (state == PipeState.CLOSED || state == PipeState.WRITE)
            return;

        closeReadNative(descriptors[0]);

        if (state == PipeState.READ_WRITE)
            state = PipeState.WRITE;
        else if (state == PipeState.READ)
            state = PipeState.CLOSED;
    }

    @Override
    public void closeWrite() {
        if (state == PipeState.CLOSED || state == PipeState.READ)
            return;

        closeWriteNative(descriptors[1]);

        if (state == PipeState.READ_WRITE)
            state = PipeState.READ;
        else if (state == PipeState.WRITE)
            state = PipeState.CLOSED;
    }

    private static native void init(int[] descriptors);
    private static native void closeReadNative(int readDescriptor);
    private static native void closeWriteNative(int writeDescriptor);

    private static native int read(int readDescriptor, byte[] buffer);
    private static native int write(int writeDescriptor, byte[] buffer);

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
                len = UnnamedPipe.read(descriptors[0], buffer);
                pos = 0;
                wasRead = true;
            }

            if (pos > len)
                return -1;
            return buffer[pos++];
        }

        @Override
        public void close() {
            UnnamedPipe.this.closeRead();
        }
    }

    private class PipeOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            if (!state.writable())
                throw new IOException("Pipe is not writable");

            byte[] buffer = {(byte) (b & 0xFF)};
            int result = UnnamedPipe.write(descriptors[1], buffer);

            if (result < 0)
                throw new IOException("Pipe writing error");
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (!state.writable())
                throw new IOException("Pipe is not writable");

            int result = UnnamedPipe.write(descriptors[1], b);

            if (result < 0)
                throw new IOException("Pipe writing error");
        }

        @Override
        public void close() {
            UnnamedPipe.this.closeWrite();
        }
    }


    static {
        Loader.loadNativeLibrary();
    }
}
