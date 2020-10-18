package os.process;

import os.process.pipe.ReadPipe;
import os.process.pipe.WritePipe;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Process {

    private final int PID;
    private static String startTime;
    private static String command = "";
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
        this.startTime = getTime();
    }

    public Process(int PID, ReadPipe readPipe, WritePipe writePipe) {
        this.PID = PID;
        this.readPipe = readPipe;
        this.writePipe = writePipe;
        this.startTime = getTime();
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
        command = name;
        return run(name.getBytes());
    }

    private static native int run(byte[] name);

    public static int runOnBackground(String name) {
        //System.out.println("backgound");
        Runtime.forkWithPipes();
        if (Runtime.getForkStatus().equals(Runtime.ForkStatus.PARENT)) {
            //System.out.println(Runtime.getChildren().size());
            return Runtime.getChildren().get(Runtime.getChildren().size() - 1).run(name);
        }else{
            return -1;
        }
    }

    public static String getUser(){
        return new String(user(), StandardCharsets.UTF_8);
    }

    private static native byte[] user();

    private static String getTime(){
        return new String(time(), StandardCharsets.UTF_8);
    }

    private static native byte[] time();

    public static String getStartTime(){
        return startTime;
    }

    public static String getCommand(){
        return command;
    }

    public String getProperties(){
        return getUser()+" "+getPID()+" "+getStartTime()+" "+getCommand();
    }

}
