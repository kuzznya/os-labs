package os.socket;

import os.utils.Loader;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public Socket(Domain domain, SocketType type) {
        this(domain, type, 0);
    }

    public Socket(Domain domain, SocketType type, int protocol) {
        this.domain = domain;
        this.type = type;
        this.protocol = protocol;
    }

    protected Socket(Domain domain, SocketType type, int protocol, int descriptor) {
        this(domain, type, protocol);
        this.descriptor = descriptor;
        this.created = true;
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

    public void create() {
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

    static {
        Loader.loadNativeLibrary();
    }


    // FIXME: 05.10.2020 IS NOT WORKING
    private class SocketInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            if (closed)
                throw new IOException("Socket is closed");

            byte[] buffer = new byte[1];
            int len = recv(descriptor, buffer);

//            System.out.print((char) buffer[0]);
//            System.out.println(" LEN=" + len);

            if (len == 0 || len == -1) {
                System.out.println("END"); return -1;}
            else
                return buffer[0];
        }

        @Override
        public int read(byte[] b) throws IOException {
            if (closed)
                throw new IOException("Socket is closed");

            int len = recv(descriptor, b);

            if (len == 0)
                return -1;
            else
                return len;
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
}
