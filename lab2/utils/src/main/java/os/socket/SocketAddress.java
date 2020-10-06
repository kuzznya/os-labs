package os.socket;

import os.utils.Loader;

import java.util.Arrays;

public class SocketAddress {
    private final Domain domain;
    private final byte[] data;

    public SocketAddress(Domain domain, byte[] data) {
        if (data.length < 14)
            throw new IllegalArgumentException("Socket address total size is 16 bytes");
        this.domain = domain;
        this.data = data;
    }

    public Domain getDomain() {
        return domain;
    }

    public byte[] getAddressData() {
        return Arrays.copyOf(data, data.length);
    }


    static {
        Loader.loadNativeLibrary();
    }
}
