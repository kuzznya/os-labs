package os.socket;

import os.utils.Loader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class InetSocketAddress extends SocketAddress {

    private final short port;

    private final int ip;

    public static final int INADDR_ANY = getInaddrAny();
    public static final int INADDR_LOOPBACK = getInaddrLoopback();

    public InetSocketAddress(short port, int ip) {
        super(Domain.AF_INET, createData(port, ip));
        this.port = port;
        this.ip = ip;
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
    private static native int getInaddrAny();
    private static native int getInaddrLoopback();

    static {
        Loader.loadNativeLibrary();
    }
}
