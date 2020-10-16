package os.process;

import os.process.pipe.ReadPipe;
import os.process.pipe.WritePipe;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Process {

    private final int PID;

    private ReadPipe readPipe = null;
    private WritePipe writePipe = null;

private enum Status{
    Running,
    Sleeping,
    Stopped,
    Zombie;
}

    public Process(int PID) {
        this.PID = PID;
    }

    public Process(int PID, ReadPipe readPipe, WritePipe writePipe) {
        this.PID = PID;
        this.readPipe = readPipe;
        this.writePipe = writePipe;
    }

    public int getPID() {
        return PID;
    }

    public Optional<ReadPipe> getReadPipe() {
        return Optional.ofNullable(readPipe);
    }

    public Optional<WritePipe> getWritePipe() {
        return Optional.ofNullable(writePipe);
    }

    public void kill() {
        Runtime.kill(PID);
    }

    public static int run(String name){
        return run(name.getBytes());
    }

    private static native int run(byte[] name);

    public static int runOnBackground(String name) {
        System.out.println("backgound");
        Runtime.forkWithPipes();
        if (Runtime.getForkStatus().equals(Runtime.ForkStatus.PARENT)) {
            System.out.println(Runtime.getChildren().size());
            return Runtime.getChildren().get(Runtime.getChildren().size() - 1).run(name);
        }else{
            return -1;
        }
    }
    public static String getUser(){
        return new String(user(), StandardCharsets.UTF_8);
    }
    private static native byte[] user();
}
