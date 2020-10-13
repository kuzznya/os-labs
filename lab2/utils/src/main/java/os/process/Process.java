package os.process;

import os.process.pipe.ReadPipe;
import os.process.pipe.WritePipe;

import java.util.Optional;

public class Process {

    private final int PID;

    private ReadPipe readPipe = null;
    private WritePipe writePipe = null;

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
}
