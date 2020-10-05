package os.socket;

import os.utils.Loader;

import java.io.Closeable;
import java.util.Arrays;

public class Socket implements Closeable {

    private final Domain domain;
    private final SocketType type;
    private final int protocol;

    private boolean created = false;
    protected int descriptor = -1;

    public Socket(Domain domain, SocketType type) {
        this(domain, type, 0);
    }

    public Socket(Domain domain, SocketType type, int protocol) {
        this.domain = domain;
        this.type = type;
        this.protocol = protocol;
    }

    public void create() {
        if (created)
            return;
        descriptor = socket(domain.getNativeValue(), type.getNativeValue(), protocol);
        if (descriptor < 0)
            throw new RuntimeException("Cannot create socket");
        created = true;
    }

    public void bind(SocketAddress address) {
        int result = bind(descriptor, domain.getNativeValue(), address.getAddressData());
        if (result < 0)
            throw new RuntimeException("Cannot bind socket");
    }

    @Override
    public void close() {
        close(descriptor);
    }

    protected static native int socket(byte domain, int type, int protocol);
    protected static native int bind(int descriptor, byte domain, byte[] addressData);
    protected static native int close(int descriptor);

    static {
        Loader.loadNativeLibrary();
    }
}
