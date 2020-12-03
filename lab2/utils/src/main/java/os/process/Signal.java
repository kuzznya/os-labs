package os.process;

import os.utils.Loader;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Signal {
    SIGHUP(getNativeValue(0)),
    SIGINT(getNativeValue(1)),
    SIGQUIT(getNativeValue(2)),
    SIGABRT(getNativeValue(3)),
    SIGKILL(getNativeValue(4)),
    SIGPIPE(getNativeValue(5)),
    SIGTERM(getNativeValue(6)),
    SIGSTOP(getNativeValue(7)),
    SIGTSTP(getNativeValue(8)),
    SIGCONT(getNativeValue(9)),
    SIGCHLD(getNativeValue(10)),
    SIGIO(getNativeValue(11)),
    SIGINFO(getNativeValue(12)),
    SIGUSR1(getNativeValue(13)),
    SIGUSR2(getNativeValue(14));

    private static final Map<Signal, Runnable> hooks = new LinkedHashMap<>();

    private final int nativeValue;

    Signal(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    public int getNativeValue() {
        return nativeValue;
    }

    public static void addHook(Signal signal, Runnable runnable) {
        hooks.put(signal, runnable);
        bindSignalHandler(signal.nativeValue);
    }

    public static void removeHook(Signal signal) {
        hooks.remove(signal);
        resetSignalHandler(signal.nativeValue);
    }

    private static Signal fromNativeValue(int signum) {
        if (signum == SIGHUP.nativeValue)
            return SIGHUP;
        if (signum == SIGINT.nativeValue)
            return SIGINT;
        if (signum == SIGQUIT.nativeValue)
            return SIGQUIT;
        if (signum == SIGABRT.nativeValue)
            return SIGABRT;
        if (signum == SIGKILL.nativeValue)
            return SIGKILL;
        if (signum == SIGPIPE.nativeValue)
            return SIGPIPE;
        if (signum == SIGTERM.nativeValue)
            return SIGTERM;
        if (signum == SIGSTOP.nativeValue)
            return SIGSTOP;
        if (signum == SIGTSTP.nativeValue)
            return SIGTSTP;
        if (signum == SIGCONT.nativeValue)
            return SIGCONT;
        if (signum == SIGCHLD.nativeValue)
            return SIGCHLD;
        if (signum == SIGIO.nativeValue)
            return SIGIO;
        if (signum == SIGINFO.nativeValue)
            return SIGINFO;
        if (signum == SIGUSR1.nativeValue)
            return SIGUSR1;
        if (signum == SIGUSR2.nativeValue)
            return SIGUSR2;
        return null;
    }

    public static void handle(int signum) {
        Signal signal = fromNativeValue(signum);
        System.out.println(signal + " RECEIVED BY " + Runtime.getCurrentProcess().getPID());
        if (signal == null)
            return;
        if (hooks.containsKey(signal))
            hooks.get(signal).run();
    }

    public static void ignore(Signal signal) {
        ignore(signal.nativeValue);
    }

    public static void raise(Signal signal) {
        raise(signal.nativeValue);
    }

    private static native int getNativeValue(int idx);
    private static native void ignore(int signum);
    private static native void raise(int signum);
    private static native void bindSignalHandler(int signum);
    private static native void resetSignalHandler(int signum);

    static {
        Loader.loadNativeLibrary();
    }
}
