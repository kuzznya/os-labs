package os.process;

import os.utils.Loader;

import java.io.Closeable;

public class Mutex implements Closeable {

    long mutex;

    public Mutex() {
        long result = init();
        if (result < 0)
            throw new RuntimeException("Mutex creation exception");
        mutex = result;
    }

    public void lock() {
        long result = lock(mutex);
        if (result < 0)
            throw new RuntimeException("Cannot lock mutex");
    }

    public void unlock() {
        long result = unlock(mutex);
        if (result < 0)
            throw new RuntimeException("Cannot unlock mutex");
    }

    public void destroy() {
        destroy(mutex);
    }

    @Override
    public void close() {
        destroy();
    }

    private static native long init();
    private static native int lock(long mutex);
    private static native int unlock(long mutex);
    private static native void destroy(long mutex);

    static {
        Loader.loadNativeLibrary();
    }
}
