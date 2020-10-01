package os.utils;

import os.utils.NativeUtils;

import java.io.IOException;

public class Loader {

    private Loader() {}

    private static OperatingSystem detectOS() {
        String name = System.getProperty("os.name").toLowerCase();

        if (name.contains("win"))
            return OperatingSystem.WINDOWS;
        if (name.contains("mac"))
            return OperatingSystem.MAC;
        if (name.contains("nix"))
            return OperatingSystem.UNIX;
        return OperatingSystem.UNSPECIFIED;
    }

    private static void loadLibraryFromJar() {
        String name;

        switch (detectOS()) {
            case WINDOWS:
                name = "bridge.dll";
                break;
            case MAC:
                name = "libbridge.dylib";
                break;
            case UNIX:
                name = "libbridge.so";
                break;
            case UNSPECIFIED:
            default:
                throw new RuntimeException("Unsupported OS");
        }

        try {
            NativeUtils.loadLibraryFromJar("/libs/" + name);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void loadNativeLibrary() {
        try {
            System.loadLibrary("bridge");
        } catch (UnsatisfiedLinkError e) {
            loadLibraryFromJar();
        }
    }
}
