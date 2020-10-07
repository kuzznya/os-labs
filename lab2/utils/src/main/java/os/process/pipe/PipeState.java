package os.process.pipe;

public enum PipeState {
    READ_WRITE,
    READ,
    WRITE,
    CLOSED;

    public boolean readable() {
        return this == PipeState.READ_WRITE || this == PipeState.READ;
    }

    public boolean writable() {
        return this == PipeState.READ_WRITE || this == PipeState.WRITE;
    }
}
