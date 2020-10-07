package os.process;

import os.utils.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrentProcess {

    private static ForkStatus forkStatus = ForkStatus.PARENT;

    private static final List<Process> children = new ArrayList<>();

    private static Pipe parentReadPipe = null;
    private static Pipe parentWritePipe = null;

    public static ForkStatus getForkStatus() {
        return forkStatus;
    }

    public static List<Process> getChildren() {
        return children;
    }

    public static Optional<Pipe> getParentReadPipe() {
        return Optional.ofNullable(parentReadPipe);
    }

    public static Optional<Pipe> getParentWritePipe() {
        return Optional.ofNullable(parentWritePipe);
    }

    public static <T> void childRun(Runnable child) {
        fork();
        if (forkStatus == ForkStatus.CHILD)
            child.run();
    }

    public static void fork() {
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
        Pipe childWritePipe = new Pipe();

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
        Pipe childReadPipe = new Pipe();
        Pipe childWritePipe = new Pipe();

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

    public enum ForkStatus {
        PARENT,
        CHILD
    }

    static {
        Loader.loadNativeLibrary();
    }
}
