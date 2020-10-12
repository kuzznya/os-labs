package os.socket;

import os.utils.Loader;

public class ClientSocket extends Socket {

    public ClientSocket() {
        super(Domain.AF_INET, SocketType.SOCK_STREAM);
    }

    public void connect(SocketAddress address) {
        int result = connect(super.descriptor,
                address.getDomain().getNativeValue(), address.getAddressData());

        if (result != 0)
            throw new RuntimeException("Cannot connect to address");
    }

    private static native int connect(int socket, byte domain, byte[] addressData);

    static {
        Loader.loadNativeLibrary();
    }

}
