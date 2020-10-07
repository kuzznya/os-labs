package os.process.pipe;

import java.io.OutputStream;

public interface WritePipe extends Pipe {
    OutputStream getOutputStream();
    void closeWrite();
}
