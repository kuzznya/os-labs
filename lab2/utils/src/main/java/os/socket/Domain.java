package os.socket;

import os.utils.Loader;

public enum Domain {
    AF_UNSPEC,
    AF_UNIX,
    AF_INET,
    AF_INET6;


    public short getNativeValue() {
        return getNativeValue(this.ordinal());
    }

    private native short getNativeValue(int idx);

    static {
        Loader.loadNativeLibrary();
    }
}
