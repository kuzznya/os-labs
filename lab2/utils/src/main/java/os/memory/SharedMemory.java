package os.memory;

import os.process.Mutex;
import os.utils.Loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SharedMemory implements Closeable {

    private static final int SIZE = 4096;

    private final String name;

    private int descriptor;

    private final long pointer;

    private final Mutex mutex;
    private boolean myMutex = false;

    private MemoryInputStream inputStream = null;
    private MemoryOutputStream outputStream = null;

    private SharedMemory(String name, boolean isNew) {
        this(name, new Mutex(), isNew);
        myMutex = true;
    }

    private SharedMemory(String name, Mutex mutex, boolean isNew) {
        this.name = name;
        this.mutex = mutex;

        mutex.lock();

        if (isNew)
            createMemory(name);
        else
            openMemory(name);

        this.pointer = map();

        mutex.unlock();
    }

    public Mutex getMutex() {
        return mutex;
    }

    private void createMemory(String name) {
        int result = createNative(name, SIZE);
        if (result < 0)
            throw new RuntimeException("Cannot create shared memory");
        this.descriptor = result;
    }

    private void openMemory(String name) {
        int result = openNative(name);
        if (result < 0)
            throw new RuntimeException("Cannot open shared memory");
        this.descriptor = result;
    }

    private long map() {
        long result = mapNative(descriptor, SIZE);
        if (result < 0)
            throw new RuntimeException("Cannot map shared memory");
        return result;
    }

    @Override
    public void close() {
        unmapNative(pointer, SIZE);
        unlinkNative(name);
        if (myMutex)
            mutex.destroy();
    }

    public InputStream getInputStream() {
        if (inputStream == null)
            inputStream = new MemoryInputStream();
        return inputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new MemoryOutputStream();
        return outputStream;
    }

    public static SharedMemory create(String name) {
        return new SharedMemory(name, true);
    }

    public static SharedMemory create(String name, Mutex mutex) {
        return new SharedMemory(name, mutex, true);
    }

    public static SharedMemory open(String name, Mutex mutex) {
        return new SharedMemory(name, mutex, false);
    }

    private static native int createNative(String name, int size);
    private static native int openNative(String name);
    private static native long mapNative(int descriptor, int size);
    private static native void unmapNative(long pointer, int size);
    private static native void unlinkNative(String name);
    private static native byte[] readNative(long pointer);
    private static native void writeNative(long pointer, byte[] data, int memorySize);


    private class MemoryInputStream extends InputStream {

        private byte[] buffer;
        int pos = 0;
        boolean wasRead = false;

        @Override
        public int read() {
            mutex.lock();

            if (pos >= buffer.length || !wasRead) {
                System.out.println("READ NATIVE");
                buffer = readNative(pointer);
                pos = 0;
                wasRead = true;
                if (buffer.length == 0)
                    return -1;
            }

            byte result = buffer[pos++];

            mutex.unlock();

            return result;
        }
    }

    private class MemoryOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            mutex.lock();
            byte[] buffer = new byte[] {(byte) (b & 0xFF)};
            writeNative(pointer, buffer, SIZE);
            mutex.unlock();
        }

        @Override
        public void write(byte[] b) throws IOException {
            mutex.lock();
            writeNative(pointer, b, SIZE);
            mutex.unlock();
        }
    }


    static {
        Loader.loadNativeLibrary();
    }

}
