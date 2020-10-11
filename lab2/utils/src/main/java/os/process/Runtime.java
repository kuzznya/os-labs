package os.process;

import os.process.pipe.UnnamedPipe;
import os.utils.Loader;

import java.util.*;

public class Runtime {

    private static ForkStatus forkStatus = ForkStatus.PARENT;

    private static final List<Process> children = new ArrayList<>();

    private static UnnamedPipe parentReadPipe = null;
    private static UnnamedPipe parentWritePipe = null;

    private static Map<String, Runnable> shutdownHooks = new LinkedHashMap<>();

    public static ForkStatus getForkStatus() {
        return forkStatus;
    }

    public static List<Process> getChildren() {
        return children;
    }

    public static Optional<UnnamedPipe> getParentReadPipe() {
        return Optional.ofNullable(parentReadPipe);
    }

    public static Optional<UnnamedPipe> getParentWritePipe() {
        return Optional.ofNullable(parentWritePipe);
    }

    public static <T> void childRun(Runnable child) {
        fork();
        if (forkStatus == ForkStatus.CHILD)
            child.run();
    }

    public static void fork() {
        System.out.println("FORK");
        int result = callFork();
        if (result == 0) {
            forkStatus = ForkStatus.CHILD;
            children.clear();
        }
        else if (result > 0) {
            forkStatus = ForkStatus.PARENT;
            children.add(new Process(result));
        }
        else
            throw new RuntimeException("Process forking failed");
    }

    public static void forkWithPipe() {
        UnnamedPipe childWritePipe = new UnnamedPipe();

        int result = callFork();

        if (result == 0) {
            forkStatus = ForkStatus.CHILD;
            children.clear();

            parentReadPipe = childWritePipe;
            parentWritePipe = null;
        }
        else if (result > 0) {
            forkStatus = ForkStatus.PARENT;
            children.add(new Process(result, null, childWritePipe));
        }
        else
            throw new RuntimeException("Process forking failed");
    }

    public static void forkWithPipes() {
        UnnamedPipe childReadPipe = new UnnamedPipe();
        UnnamedPipe childWritePipe = new UnnamedPipe();

        int result = callFork();

        if (result == 0) {
            forkStatus = ForkStatus.CHILD;
            children.clear();

            parentReadPipe = childWritePipe;
            parentReadPipe.closeWrite();
            parentWritePipe = childReadPipe;
            parentWritePipe.closeRead();
        }
        else if (result > 0) {
            forkStatus = ForkStatus.PARENT;

            childReadPipe.closeWrite();
            childWritePipe.closeRead();

            children.add(new Process(result, childReadPipe, childWritePipe));
        }
        else
            throw new RuntimeException("Process forking failed");
    }

    public static native void kill(int PID);
    public static native void kill(int PID, int signal);

    private static native int callFork();

    private static void addShutdownHook(String name, Runnable hook) {
        shutdownHooks.put(name, hook);
    }

    private static void removeShutdownHook(String name, Runnable hook) {
        shutdownHooks.remove(name);
    }

    public static void exit() {
        System.out.println("Exiting now");
        try {
            shutdownHooks.values().forEach(Runnable::run);
        } finally {
            java.lang.Runtime.getRuntime().halt(0);
        }
    }

    public enum ForkStatus {
        PARENT,
        CHILD
    }

    static {
        Loader.loadNativeLibrary();
    }
}
