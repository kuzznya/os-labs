package os.socket;

import os.utils.Loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Socket implements Closeable {

    private final Domain domain;
    private final SocketType type;
    private final int protocol;

    private boolean created = false;
    private boolean bound = false;
    private boolean closed = false;

    protected int descriptor = -1;

    private SocketAddress boundAddress;

    private SocketInputStream inputStream = null;
    private SocketOutputStream outputStream = null;

    private Socket(Domain domain, SocketType type, int protocol, boolean created) {
        this.domain = domain;
        this.type = type;
        this.protocol = protocol;
        this.created = created;

        if (!created)
            init();
    }

    public Socket(Domain domain, SocketType type) {
        this(domain, type, 0, false);
    }

    public Socket(Domain domain, SocketType type, int protocol) {
        this(domain, type, protocol, false);
    }

    protected Socket(Domain domain, SocketType type, int protocol, int descriptor) {
        this(domain, type, protocol, true);
        this.descriptor = descriptor;
    }

    protected Socket(Domain domain, SocketType type, int protocol, int descriptor, SocketAddress address) {
        this(domain, type, protocol, descriptor);
        this.boundAddress = address;
        this.bound = true;
    }

    public Domain getDomain() {
        return domain;
    }

    public SocketType getType() {
        return type;
    }

    public int getProtocol() {
        return protocol;
    }

    public void init() {
        if (created)
            return;
        descriptor = socket(domain.getNativeValue(), type.getNativeValue(), protocol);
        if (descriptor < 0)
            throw new RuntimeException("Cannot create socket");
        created = true;
    }

    public void bind(SocketAddress address) {
        if (bound)
            return;
        int result = bind(descriptor, domain.getNativeValue(), address.getAddressData());
        if (result < 0)
            throw new RuntimeException("Cannot bind socket");
        this.boundAddress = address;
    }

    public InputStream getInputStream() {
        if (inputStream == null)
            inputStream = new SocketInputStream();
        return inputStream;
    }

    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new SocketOutputStream();
        return outputStream;
    }

    @Override
    public void close() {
        close(descriptor);
        this.closed = true;
    }

    protected static native int socket(byte domain, int type, int protocol);
    protected static native int bind(int descriptor, byte domain, byte[] addressData);
    protected static native int recv(int descriptor, byte[] buffer);
    protected static native int send(int descriptor, byte[] buffer);
    protected static native int close(int descriptor);


    private class SocketInputStream extends InputStream {

        private static final int BUFFER_SIZE = 4096;
        private byte[] buffer = new byte[BUFFER_SIZE];
        int pos = 0;
        int len = 0;
        boolean wasRead = false;

        SocketInputStream() {
            Arrays.fill(buffer, (byte) -1);
        }

        @Override
        public int read() throws IOException {
            if (closed)
                throw new IOException("Socket is closed");

            if (len == BUFFER_SIZE && pos == BUFFER_SIZE || !wasRead) {
                len = recv(descriptor, buffer);
                pos = 0;
                wasRead = true;
            }

            if (pos >= len)
                return -1;
            return buffer[pos++];
        }
    }

    private class SocketOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            if (closed)
                throw new IOException("Socket is closed");

            byte[] buffer = {(byte) (b & 0xFF)};
            int len = send(descriptor, buffer);

            if (len < 1)
                throw new IOException("Socket sending error");
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (closed)
                throw new IOException("Socket is closed");

            int len = send(descriptor, b);

            if (len < b.length)
                throw new IOException("Socket sending error");
        }
    }


    static {
        Loader.loadNativeLibrary();
    }
}
