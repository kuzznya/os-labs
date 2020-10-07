package os.process;

import java.util.Optional;

public class Process {

    private final int PID;

    private Pipe readPipe = null;
    private Pipe writePipe = null;

    public Process(int PID) {
        this.PID = PID;
    }

    public Process(int PID, Pipe readPipe, Pipe writePipe) {
        this.PID = PID;
        this.readPipe = readPipe;
        this.writePipe = writePipe;
    }

    public int getPID() {
        return PID;
    }

    public Optional<Pipe> getReadPipe() {
        return Optional.ofNullable(readPipe);
    }

    public Optional<Pipe> getWritePipe() {
        return Optional.ofNullable(writePipe);
    }

    public void kill() {
        CurrentProcess.kill(PID);
    }
}
