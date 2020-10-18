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
        System.out.println("fork");
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

    public static void addShutdownHook(String name, Runnable hook) {
        shutdownHooks.put(name, hook);
    }

    public static void removeShutdownHook(String name, Runnable hook) {
        shutdownHooks.remove(name);
    }

    public static void exit() {
        // FIXME: 12.10.2020 child processes are not exiting
        try {
            shutdownHooks.values().forEach(Runnable::run);
        } finally {
            java.lang.Runtime.getRuntime().halt(0);
        }
    }

    public static void commitSuicide() {
        try {
            shutdownHooks.values().forEach(Runnable::run);
        } finally {
            Signal.raise(Signal.SIGKILL);
        }
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
    public static void processProperties(){
        if(getChildren().size()==0){
            return;
        }else{
            for(int i=0; i<getChildren().size();i++){
                System.out.println(getChildren().get(i).getProperties());
            }
        }
    }
}
