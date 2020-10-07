package os.process.pipe;

import java.io.Closeable;

public interface Pipe extends Closeable {
    PipeState getState();
}
