package os.socket;

import os.utils.Loader;

import java.util.Arrays;

public class ServerSocket extends Socket {

    public ServerSocket() {
        super(Domain.AF_INET, SocketType.SOCK_STREAM);
    }

    public ServerSocket(short port) {
        super(Domain.AF_INET, SocketType.SOCK_STREAM);
        bind(new InetSocketAddress(port, InetSocketAddress.INADDR_ANY));
    }

    public void listen() {
        int result = listen(this.descriptor);
        if (result < 0)
            throw new RuntimeException("Error occurred while trying to listen to connections");
    }

    public Socket accept() {
        byte[] data = new byte[16];
        int clientDescriptor = accept(this.descriptor, data);
        if (clientDescriptor < 0)
            throw new RuntimeException("Cannot accept incoming connection");

        byte domainValue = data[1];
        Domain clientDomain = Domain.fromNativeValue(domainValue);
        SocketAddress clientAddress = new SocketAddress(clientDomain, Arrays.copyOfRange(data, 2, 16));
        return new Socket(clientDomain, SocketType.SOCK_STREAM, 0, clientDescriptor, clientAddress);
    }

    protected static native int listen(int descriptor);
    protected static native int listen(int descriptor, int backlog);

    protected static native int accept(int descriptor, byte[] buffer);

    static {
        Loader.loadNativeLibrary();
    }
}
