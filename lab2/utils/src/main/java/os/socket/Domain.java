package os.socket;

import os.utils.Loader;

public enum Domain {
    AF_UNSPEC(getNativeValue(0)),
    AF_UNIX(getNativeValue(1)),
    AF_INET(getNativeValue(2)),
    AF_INET6(getNativeValue(3));

    private final byte nativeValue;

    Domain(byte nativeValue) {
        this.nativeValue = nativeValue;
    }

    public static Domain fromNativeValue(byte nativeValue) {
        if (nativeValue == AF_UNSPEC.getNativeValue())
            return AF_UNSPEC;
        if (nativeValue == AF_UNIX.getNativeValue())
            return AF_UNIX;
        if (nativeValue == AF_INET.getNativeValue())
            return AF_INET;
        if (nativeValue == AF_INET6.getNativeValue())
            return AF_INET6;
        return AF_UNSPEC;
    }

    public byte getNativeValue() {
        return this.nativeValue;
    }

    private static native byte getNativeValue(int idx);

    static {
        Loader.loadNativeLibrary();
    }
}
