package os.socket;

import os.utils.Loader;

public enum SocketType {
    SOCK_STREAM,
    SOCK_DGRAM,
    SOCK_RAW,
    SOCK_SEQPACKET;

    public int getNativeValue() {
        return getNativeValue(this.ordinal());
    }

    private native int getNativeValue(int idx);

    static {
        Loader.loadNativeLibrary();
    }
}
