package os.socket;

import os.utils.Loader;

import java.util.Arrays;

public class ServerSocket extends Socket {

    public ServerSocket() {
        super(Domain.AF_INET, SocketType.SOCK_STREAM);
    }

    public void listen() {
        int result = listen(this.descriptor, 5);
        if (result < 0)
            throw new RuntimeException("Error occurred while trying to listen to connections");
    }

    public SocketAddress accept() {
        byte[] data = accept(this.descriptor);
        if (data.length < 16)
            throw new RuntimeException("Error occurred while trying to accept connection");
        byte domainValue = data[1];
        Domain clientDomain = Domain.fromNativeValue(domainValue);
        return new SocketAddress(clientDomain, Arrays.copyOfRange(data, 2, 16));
    }

    protected static native int listen(int descriptor, int backlog);

    protected static native byte[] accept(int descriptor);

    static {
        Loader.loadNativeLibrary();
    }
}
