package os.socket;

import os.utils.Loader;

import java.io.Closeable;

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
        created = true;
    }

    public void bind(SocketAddress address) {
        bind(descriptor, domain.getNativeValue(), address.toCharArray());
    }

    @Override
    public void close() {
        close(descriptor);
    }

    protected native int socket(short domain, int type, int protocol);
    protected native int bind(int descriptor, short domain, char[] addressData);
    protected native int close(int descriptor);

    static {
        Loader.loadNativeLibrary();
    }
}
