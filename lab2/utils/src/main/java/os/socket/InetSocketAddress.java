package os.socket;

import os.utils.Loader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InetSocketAddress extends SocketAddress {

    private final short port;

    private final int ip;

    public InetSocketAddress(short port, int ip) {
        super(Domain.AF_INET, createData(port, ip));
        this.port = port;
        this.ip = ip;
    }

    // NOTE INADDR_ANY is set to 0 BUT this should be checked in netinet/in.h
    public InetSocketAddress(short port) {
        this(port, 0);
    }

    public short getPort() {
        return port;
    }

    public int getIp() {
        return ip;
    }

    private static byte[] createData(short port, int ip) {
        return ByteBuffer.wrap(new byte[14])
                .order(ByteOrder.BIG_ENDIAN)
                .putShort(port)
                .putInt(ip)
                .array();
    }

    private static native byte[] getNativeData(short port, int ip);

    static {
        Loader.loadNativeLibrary();
    }
}
