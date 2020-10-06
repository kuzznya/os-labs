package os.process;

import os.utils.Loader;

public class Process {

    private static Status processStatus = Status.PARENT;

    public static Status getStatus() {
        return processStatus;
    }

    public static <T> void childRun(Runnable child) {
        fork();
        if (processStatus == Status.CHILD)
            child.run();
    }

    public static void fork() {
        int result = callFork();
        if (result == 0)
            processStatus = Status.CHILD;
        else if (result > 0)
            processStatus = Status.PARENT;
        else
            throw new RuntimeException("Process forking failed");
    }

    private static native int callFork();

    public enum Status {
        PARENT,
        CHILD
    }

    static {
        Loader.loadNativeLibrary();
    }
}
